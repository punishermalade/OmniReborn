package com.icerealm.omnireborn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icerealm.omnireborn.game.GameService;
import com.icerealm.omnireborn.game.GameViewModel;
import com.icerealm.omnireborn.json.GameViewModelTranslator;
import com.icerealm.omnireborn.json.InvalidCommand;
import com.icerealm.omnireborn.json.PlayerListTranslator;
import com.icerealm.omnireborn.json.UnitTranslator;
import com.icerealm.omnireborn.map.MapService;
import com.icerealm.omnireborn.map.MapService.TileType;
import com.icerealm.omnireborn.map.MapViewModel;
import com.icerealm.omnireborn.map.PathFinder;
import com.icerealm.omnireborn.map.TileViewModel;
import com.icerealm.omnireborn.unit.UnitService;
import com.icerealm.omnireborn.unit.UnitViewModel;
import com.icerealm.omnireborn.user.OmniUser;
import com.icerealm.omnireborn.user.UserService;
import com.icerealm.omnireborn.util.JsonTranslator;
import com.icerealm.omnireborn.util.Point;

/**
 * Controls the flow between the OmniHandler and the different game component.
 * could be a controller chain request
 * @author neilson
 *
 */
public class ActionController {
	
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * used to build unique name. This is not the real user Id.
	 */
	private static int USER_ID = 0;
	
	/**
	 * Place holder to keep track which user gets which message
	 */
	private Map<OmniUser, JsonObject> _recipients = null;
	
	/**
	 * the map service to manage the maps between players
	 */
	private MapService _mapService = null;
	
	/**
	 * a unit that creates and manages units in the game
	 */
	private UnitService _unitService = null;
	
	/**
	 * Use an interface to standardize the translator of viewmodel into json
	 */
	private JsonTranslator _jsonTranslator = null;
	
	/**
	 * a game service to manage the creation of games
	 */
	private GameService _gameService = null;
	
	/**
	 * a user service to manage the connected user
	 */
	private UserService _userService = null;
	
	/**
	 * a service that calculate paths for moving units
	 */
	private PathFinder _pathFinder = null;
	
	/**
	 * Initialize the private class members
	 */
	public ActionController() {		
		_recipients = new HashMap<OmniUser, JsonObject>();
		_mapService = new MapService();
		_unitService = new UnitService();
		_jsonTranslator = new JsonTranslator();
		_gameService = new GameService();
		_userService = new UserService();
		_pathFinder = new PathFinder();
	}
	
	/**
	 * Create a new user.
	 * @return a new OmniUser instance
	 */
	public OmniUser createNewUser() {
		OmniUser user = new OmniUser("User" + USER_ID++);
		_userService.addUser(user);
		return user;
	}
	
	public Map<OmniUser, JsonObject> userDisconnected(OmniUser user) {
		// this user is gone forever
		_userService.removeUser(user);
		
		// manage the current game of this player
		return playerQuitGameEvent(user);
	}

	/**
	 * Decode the request from a player that went through the Web Socket Handler
	 * @param request The request sent by the user
	 * @param user The user that sent it
	 * @param listUsers the list of current users
	 * @return the content that each user should receive
	 */
	public Map<OmniUser, JsonObject> getOutputResult(JsonObject request, OmniUser user, List<OmniUser> listUsers) {
		// clear the last request
		_recipients.clear();
		String action = null;
		
		// logging what the controller received
		this.logUserActionContent(Level.INFO, user, "SENT", request.toString());
		
		// check if the player is the active player
		GameViewModel game = _gameService.findGameByUserId(user.getId());
		boolean requestFromActivePlayerValid = false;

		// checking if the game is null, which means the player is not in a game OR
		// the active player is not null AND this is the active player
		if (game == null || (game.getActivePlayer() != null && game.getActivePlayer().equals(user))) {
			requestFromActivePlayerValid = true;
		}

		// check for active user and request contains an action item
		// if not from correct player, message sent to this player only
		if (requestFromActivePlayerValid && request.has("action")) {
			
			action = request.get("action").getAsString();

			if (action.equalsIgnoreCase("players")) {
				String[] playersName = getPlayersName(listUsers);
				
				// build the json object
				JsonObject json = _jsonTranslator.translate(new PlayerListTranslator(playersName)).getAsJsonObject();
				json.addProperty("action", "list");
				
				// the requester is the recipient
				this.logUserActionContent(Level.FINE, user, "RECV", json.toString());
				this.setUserAsRecipient(user, json);
			}
			else if (action.equalsIgnoreCase("end-turn")) {
				
				// the player indicate that his turn his done
				game.setPlayerTurnDone(user.getId(), true);
				LOGGER.info(user + " has finished his current turn - total done in current game: " + game + " " + game.getTurnDone());
				
				if (game.getTurnCompleted().values().contains(false)) {
					// end of turn!
					LOGGER.info(user + " said he completed his turn, next player is " + game.getNextActivePlayer());
				}
				else {
					LOGGER.info("end of turn for the game, resetting for the beggining of a turn");
					prepareGameForNextTurn(game);
				}
				
				JsonObject nextTurn = new JsonObject();
				nextTurn.addProperty("yourturn", true);
				this.logUserActionContent(Level.FINE, game.getActivePlayer(), "RECV", nextTurn.toString());
				this.setUserAsRecipient(game.getActivePlayer(), nextTurn);
				
			}
			else if (action.equalsIgnoreCase("player-quit")) {
				_recipients = playerQuitGameEvent(user);
			}
			else if (action.equalsIgnoreCase("start-game")) {
				
				// current user is considered in game now
				_userService.getUser(user.getId()).setInGame(true);
				
				// create a default game
				game = _gameService.createDefaultGame();
				
				// need to find another player first
				Set<OmniUser> users = new HashSet<OmniUser>();				
				OmniUser anotherPlayer = _userService.findAvailableUser();
				
				if (anotherPlayer != null) {
					
					LOGGER.info("User " + user.getId() + " searched for available player, other player is User " + anotherPlayer.getId());
					
					// assign this player the gameviewmodel, his opponent
					anotherPlayer.setInGame(true);
					users.add(user);
					users.add(anotherPlayer);
					game.setUsers(users);
					
					
					// add the players to the list of end of turn
					game.setAllPlayerTurnDone(false);
					
					// assign an active player now, the game will begin soon
					game.getNextActivePlayer();
					
					// register
					_gameService.registerUserIdForExistingGame(user.getId(), game.getGameId());
					_gameService.registerUserIdForExistingGame(anotherPlayer.getId(), game.getGameId());
					
					// create a map
					MapViewModel map = _mapService.generateNewRandomMap();
					game.setCurrentMap(map);
					
					// add the HQ and first units
					this.createFirstUnits(new OmniUser[] { user, anotherPlayer }, map, game);
						
					// translate into json
					JsonObject result = _jsonTranslator.translate(new GameViewModelTranslator(game)).getAsJsonObject();
					result.addProperty("action", "begin-game");
					result.addProperty("valid", true);

					// the player that initiated the game and the found player
					for (OmniUser players : game.getUsers()) {
						logUserActionContent(Level.FINE, players, "RECV", "Game data...");
						logUserActionContent(Level.FINER, players, "RECV", result.toString());
						this.setUserAsRecipient(players, result);
					}
				}
				else {
					// no other player found!
					JsonObject object = new JsonObject();
					object.addProperty("action", "begin-game");
					object.addProperty("valid", false);
					object.addProperty("reason", "no other player found!");
					user.setInGame(false);
					this.setUserAsRecipient(user, object);
				}
			}
			else if (action.equalsIgnoreCase("move-unit")) {
				boolean gameOver = false;
				boolean checkUnitCount = false;
				boolean validMove = false;
				boolean allPlayerDone = false;
				boolean isPlayerTurnDone = true;
				OmniUser nextPlayer = null;
				
				Map<Integer, Integer> unitsCountByPlayer = new HashMap<Integer, Integer>();
				JsonObject result = new JsonObject();
				
				// get the new unit position
				int unitId = request.get("unitid").getAsInt();
				int newPosX = request.get("posX").getAsInt();
				int newPosY = request.get("posY").getAsInt();
				Point destination = new Point(newPosX, newPosY);
				
				// getting all the object from their respective services
				game = _gameService.findGameByUserId(user.getId());
				UnitViewModel movingUnit = _unitService.getUnit(unitId);
				TileViewModel destTile = game.getCurrentMap().getTiles().get(destination);
				UnitViewModel destinationUnit = _unitService.getUnitAtPosition(destination, game.getUnits());							 
				
				// good owner, unit can move, tile is not water
				if (user.getId() == movingUnit.getUserId() && movingUnit.getMovementLeft() > 0 && destTile.getType() != TileType.WATER.ordinal()) {
					
					// checking if the destination tile is empty OR contains an enemy unit, in that case
					// this can be a valid move (if the unit is able to get there).
					// in case this is not a valid, it also means the destination is a friendly unit and no move
					// is allowed in any case. Path will be empty, nothing will be moved.
					List<Point> path = new ArrayList<Point>();
					boolean enemyUnitPresent = false;
					if (destinationUnit == null || (enemyUnitPresent = (destinationUnit != null && destinationUnit.getUserId() != user.getId()))) {
						path = calculateBestPath(destination, movingUnit, game.getCurrentMap());
					}
					
					// checking if we have a valid path
					if (!path.isEmpty()) {
						
						// checking if a enemy unit is there, if false it will be normal move
						if (enemyUnitPresent) {
							
							// enemy unit found, it will return the winner
							UnitViewModel winner = unitAttackAnotherUnit(movingUnit, destinationUnit, destTile);
							
							// attacker won!
							if (winner != null && winner.equals(movingUnit)) {
								LOGGER.fine("Combat result: attacker " + winner + " is victorious");
								
								// need to remove the other unit from the game
								_unitService.removeUnit(destinationUnit.getId());
								game.getUnits().remove(destinationUnit);
								checkUnitCount = true;
								
								// move the winner in position
								movingUnit.getPosition().setX(newPosX);
								movingUnit.getPosition().setY(newPosY);
								
								// building the result
								validMove = true;
								result = _jsonTranslator.translate(new UnitTranslator(movingUnit)).getAsJsonObject();
								result.addProperty("valid", validMove);		
							}
							else if (winner != null && winner.equals(destinationUnit)) {
								
								// defender won!
								LOGGER.fine("Combat result: defender " + winner + " is victorious");
								
								// need to remove the attacker from the game
								_unitService.removeUnit(movingUnit.getId());
								game.getUnits().remove(movingUnit);
								checkUnitCount = true;							
								
								// building the result
								validMove = true;
								result.addProperty("defenderwon", movingUnit.getId());
								result.addProperty("posX", movingUnit.getPosition().getX());
								result.addProperty("posY", movingUnit.getPosition().getY());
								result.addProperty("valid", validMove);
							}
							else {
								LOGGER.fine("Combat result draw! no winner, no unit lost!");
							}
							
							// need to check if we need to check the number of units by each player
							// the player with no units left lose, this condition is true if
							// a unit was destroyed
							if (checkUnitCount) {
															
								// counting units for all player, it is possible that an attacker
								// killed more than one unit
								
								for (OmniUser u : game.getUsers()) {
									int countUnit = _unitService.getCountUnitForPlayer(u.getId(), game.getUnits());
									LOGGER.fine(u + " has " + countUnit + " units");
									if (countUnit == 0) {
										unitsCountByPlayer.put(u.getId(), countUnit);
									}
								}
								
								// if a player lost, game over
								gameOver = unitsCountByPlayer.size() > 0;
							}
						}
						else {
							// this move did not have any combat logic, easy and simple move
							// change the position of the moving unit
							validMove = true;
							movingUnit.getPosition().setX(newPosX);
							movingUnit.getPosition().setY(newPosY);
							
							// set the result to the user
							result = _jsonTranslator.translate(new UnitTranslator(movingUnit)).getAsJsonObject();
							result.addProperty("valid", validMove);
						}
						
						// validating if all units are done from moving, we assume the turn
						// is over until you find a unit with movement point left
						
						Iterator<UnitViewModel> iterator = game.getUnits().iterator();
						
						while (iterator.hasNext() && isPlayerTurnDone) {
							UnitViewModel unit = iterator.next();
							
							if (unit.getUserId() == user.getId() && unit.getMovementLeft() > 0) {
								isPlayerTurnDone = false;
							}
						}
						
						// checking if the player has his turn completed
						if (isPlayerTurnDone) {
							game.setPlayerTurnDone(user.getId(), true);
							nextPlayer = game.getNextActivePlayer();
							LOGGER.info("Next player to play is: " + nextPlayer);
							
							
						}
						
						// checking if all the player are done
						if (!game.getTurnCompleted().containsValue(false)) {
							allPlayerDone = true;
							LOGGER.info(game + " completed another turn - " + game.getTurnDone() + " turns done");
						}
					}
					else {
						result.addProperty("valid", validMove);
						result.addProperty("reason", "Could not find a valid path");
						LOGGER.fine("User " + user.getId() + " moved the unit " + unitId + " but the path is invalid");
					}
					
					
					
				}
				else {
					result.addProperty("valid", validMove);
					result.addProperty("reason", "Not the unit owner OR no movement point left OR invalid tile");
					LOGGER.fine("User " + user.getId() + " moved the unit " + unitId + " cannot be done!");
				}
				
				// send the move-unit to the clients
				result.addProperty("action", "move-unit");
			
				// checking if it was a valid move and the game is not over
				if (validMove && !gameOver) {
					
					if (allPlayerDone) {	
						
						LOGGER.fine("all player are done, reseting the game for a new turn");
						
						// valid move nobody lost, attack might have occured, turn not completed
						result.addProperty("turnover", true);
						
						// get the game ready for next turn
						prepareGameForNextTurn(game);
					}
										
					// sending the move and possibly the endturn property
					for (OmniUser u : game.getUsers()) {
						
						if ((isPlayerTurnDone && nextPlayer != null && nextPlayer.equals(u)) || u.equals(game.getActivePlayer())) {
							JsonObject playerTurnNotif = copyExistingJsonElement(result);
							playerTurnNotif.addProperty("yourturn", true);
							this.logUserActionContent(Level.FINE, u, "RECV", playerTurnNotif.toString());
							this.setUserAsRecipient(u, playerTurnNotif);
						}
						else {
							this.logUserActionContent(Level.FINE, u, "RECV", result.toString());
							this.setUserAsRecipient(u, result);
						}
					}					
				}
				else if (!validMove && !gameOver) {
					// that was just an invalid move
					this.setUserAsRecipient(user, result);
				}
				else if (gameOver) {
					LOGGER.info("Game over!");
					
					for (OmniUser player : game.getUsers()) {
						
						JsonObject gameOverJson = copyExistingJsonElement(result);
						gameOverJson.addProperty("gameover", true);
						
						
						if (unitsCountByPlayer.containsKey(player.getId())) {
							gameOverJson.addProperty("gameresult", "defeat");
							LOGGER.fine(player + " lost!");
						}
						else {
							gameOverJson.addProperty("gameresult", "victory");
							LOGGER.fine(player + " won!");
						}
						
						this.logUserActionContent(Level.FINE, player, "RECV", gameOverJson.toString());
						this.setUserAsRecipient(player, gameOverJson);
					}
				}
				
			}
			else if (action.equalsIgnoreCase("buy-unit")) {
				
				int x = Integer.parseInt(request.get("x").getAsString());
				int y = Integer.parseInt(request.get("y").getAsString());
				String type = request.get("type").getAsString();
				
				Point position = new Point();
				position.setX(x);
				position.setY(y);

				UnitViewModel unitExist = _unitService.getUnitAtPosition(position, game.getUnits());
				
				if (unitExist == null) {
					// creating the unit
					UnitViewModel unit = _unitService.createNewUnitAtPosition(user, position, type);		
					JsonObject result = _jsonTranslator.translate(new UnitTranslator(unit)).getAsJsonObject();
					result.addProperty("action", "new-unit");
					result.addProperty("valid", true);
					
					// need to tell all user in game
					game.getUnits().add(unit);
					
					for (OmniUser u : game.getUsers()) {
						this.logUserActionContent(Level.FINE, u, "RECV", result.toString());
						this.setUserAsRecipient(u, result);
					}
				}
				else {
					JsonObject result = new JsonObject();
					result.addProperty("action", "new-unit");
					result.addProperty("valid", false);
					result.addProperty("reason", "Unit already at this position. Select another tile");
					this.logUserActionContent(Level.FINE, user, "RECV", result.toString());
					this.setUserAsRecipient(user, result);
				}
			}
			else {
				LOGGER.info("User " + user.getId() + " SENT: invalid command: " + request.toString());
				JsonElement element = _jsonTranslator.translate(new InvalidCommand("alert", "unknown action"));
				this.logUserActionContent(Level.FINE, user, "RECV", element.toString());
				this.setUserAsRecipient(user, element.getAsJsonObject());
			}
		}
		else {
			LOGGER.info(user + " SENT: while NOT active: " + request.toString());
			JsonObject result = new JsonObject();
			result.addProperty("action", "alert");
			result.addProperty("distance", "It is not your turn! Wait for the other player to be done!");
			this.logUserActionContent(Level.INFO, user, "RECV", result.toString());
			this.setUserAsRecipient(user, result.getAsJsonObject());
		}
		
		return _recipients;
	}
	
	/**
	 * Set one user as the recipient of the message
	 * @param user the recipient
	 * @param response the message content
	 */
	private void setUserAsRecipient(OmniUser user, JsonObject response) {	
		_recipients.put(user, response);

	}
	
	/**
	 * Get the name of each player and return the result
	 * @param users the list of users
	 * @return a string array representing each player's name
	 */
	private String[] getPlayersName(List<OmniUser> users) {
		String[] result = new String[users.size()];
		int index = 0;
		for (OmniUser u : users) {
			result[index++] = u.getName();
		}
		return result;
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	private Map<OmniUser, JsonObject> playerQuitGameEvent(OmniUser user) {
		// return an answer to the handler, will propagate it to other player
		Map<OmniUser, JsonObject> recipients = new HashMap<OmniUser, JsonObject>();
		boolean valid = true;
	
		// remove the player from the game and from the game service index
		GameViewModel game = _gameService.findGameByUserId(user.getId());
	
		// check if the user was in an active game
		if (game != null) {
			
			LOGGER.info("User " + user.getId() + " was in game " + game.getGameId());
			
			// the player could be available later for another game
			user.setInGame(false);
			
			// remove the player that just quit
			game.getUsers().remove(user);
			_gameService.unregisterUserId(user.getId());
			
			// check the number of connected player
			if (!game.isEnoughPlayer()) {
				
				LOGGER.info("Game " + game.getGameId() + " has not enough player! " + 
							"Minimum # players: " + game.getMinimumPlayers() + " Actual #: " + game.getUsers().size());
				
				// game over, removing players
				for (OmniUser otherPlayer : game.getUsers()) {
					_gameService.unregisterUserId(otherPlayer.getId());
					otherPlayer.setInGame(false);
					LOGGER.fine("User " + otherPlayer.getId() + " removed from Game " + game.getGameId());
				}
				
				// removing units from the unit service
				for (UnitViewModel unit : game.getUnits()) {
					_unitService.removeUnit(unit.getId());
					LOGGER.fine("Unit " + unit.getId() + " removed from Game " + game.getGameId());
				}
				
				// removing the game from the list
				_gameService.removeExistingGame(game.getGameId());
				valid = false;
				LOGGER.info("Game " + game.getGameId() + " has been removed from the service");
			}
			
			// building the response for the client
			JsonObject result = new JsonObject();
			result.addProperty("action", "player-left-game");
			result.addProperty("userId", user.getId());
			result.addProperty("valid", valid);
			recipients.put(user, result);
			for (OmniUser u : game.getUsers()) {
				recipients.put(u, result);
			}
		}

		return recipients;
	}
	
	private List<Point> calculateBestPath(Point pos, UnitViewModel unit, MapViewModel map) {
		LOGGER.fine("calculating path for unit " + unit.getId() + " at " + unit.getPosition() + ", movement pt: " + unit.getMovement() + " destination is " + pos.toString());

		List<Point> path = new ArrayList<Point>();
		
		_pathFinder.IsDestinationValid(unit, pos, map, path);
		
		LOGGER.fine("path list has " + path.size() + " step(s)");
		int moveLeft = 0;
		for (Point p : path) {
			TileViewModel tile = map.getTiles().get(p);
			if (tile != null) {
				moveLeft = unit.getMovementLeft() -  tile.getMovementCost();
				unit.setMovementLeft(moveLeft);
				LOGGER.fine(unit + " -> " + p);
			}
			else {
				LOGGER.warning("Could not find a valid tile at " + p + " but was part of a path!");
			}
			
		}
		return path;
	}
	
	private UnitViewModel unitAttackAnotherUnit(UnitViewModel attacker, UnitViewModel defender, TileViewModel tile) {
		// creating a pseudo random
		Random r = new Random(System.currentTimeMillis());
		if (r.nextBoolean()) {
			return attacker;
		}
		return defender;
	}
	
	
	private void logUserActionContent(Level l, OmniUser user, String action, String content) {
		LOGGER.log(l, String.format("User %d %s : %s", user.getId(), action, content));
	}
	
	private JsonObject copyExistingJsonElement(JsonObject source) {
		JsonObject result = new JsonObject();
		Set<Entry<String, JsonElement>> entries = source.entrySet();
		
		for (Entry<String, JsonElement> e : entries) {
			result.add(e.getKey(), e.getValue());
		}

		return result;
	}
	
	private void prepareGameForNextTurn(GameViewModel game) { 
		
		// cleaning the player turn done information
		game.setAllPlayerTurnDone(false);
		
		// reset units movement
		for (UnitViewModel u : game.getUnits()) {							
			u.setMovementLeft(u.getMovement());
			LOGGER.fine(u + " has now " + u.getMovementLeft() + " move left!");
		}
		
		// reset the active player
		game.resetPlayerQueue();
		game.getNextActivePlayer();
		LOGGER.info("next player is: " + game.getActivePlayer());
		
	}
	
	private void createFirstUnits(OmniUser[] players, MapViewModel map, GameViewModel gameModel) {
		
		// finding the top and bottom middle of the map
		int horizontalMiddleTop = map.getWidth() / 2;
		int bottom = map.getHeight() - 1;
		int horizontalMiddleBottom = 0;
		int top = 0;
		LOGGER.fine("Found those value for first units -> middle: " + horizontalMiddleTop + " bottom: " + bottom);
		
		// calcultating two points
		Point topPosition = new Point(horizontalMiddleTop, top);
		Point bottomPosition = new Point(horizontalMiddleBottom, bottom);
		LOGGER.fine("Top: " + topPosition + " bottom: " + bottomPosition);
		
		// no validation at the moment, place the unit right there
		if (players.length > 1) {
			
			// calling the unit service to place the unit and assign them to their owner
			UnitViewModel topUnit 		= _unitService.createNewUnitAtPosition(players[0], topPosition, UnitService.UnitType.SCOUT.toString());
			UnitViewModel bottomUnit 	= _unitService.createNewUnitAtPosition(players[1], bottomPosition, UnitService.UnitType.SCOUT.toString());
			
			// add those in the game model
			gameModel.getUnits().add(topUnit);
			gameModel.getUnits().add(bottomUnit);
			
			
		}
		
		LOGGER.fine("Created the first unit for each player");		
	}
}
