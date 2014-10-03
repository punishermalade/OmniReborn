package com.icerealm.omnireborn.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Create and manage games that the player are in
 * @author neilson
 *
 */
public class GameService {

	/**
	 * represent the game id incrementor, to ensure game id uniqueness
	 */
	private static int GAME_ID = 0;
	
	/**
	 * map the user id to a game Id, useful to find the game based on user Id
	 */
	private Map<Integer, Integer> _playersInGame = null;
	
	/**
	 * represents the games currently in progress. the key is the game id
	 */
	private Map<Integer, GameViewModel> _games = null;
	
	/**
	 * default constructor
	 */
	public GameService() {
		_playersInGame = new HashMap<Integer, Integer>();
		_games = new HashMap<Integer, GameViewModel>();
	}
	
	/**
	 * create a default game with a unique id and add this instance to the list
	 * of current game
	 * @return the new instance created.
	 */
	public GameViewModel createDefaultGame() {
		GameViewModel model = new GameViewModel();
		model.setGameId(GAME_ID++);
		model.setTurnDone(0);
		_games.put(model.getGameId(), model);
		return model;
	}
	
	/**
	 * register a player for a given game
	 * @param userid the user id of the player in game
	 * @param gameid the game in which the player is playing
	 */
	public void registerUserIdForExistingGame(int userid, int gameid) {
		_playersInGame.put(userid, gameid);
	}
	
	/**
	 * remove a user from an existing game. ususally due to the player disconnected from
	 * the server or decided to quit the game.
	 * @param userid
	 */
	public void unregisterUserId(int userid) {
		_playersInGame.remove(userid);
	}
	
	/**
	 * remove an existing game. Usually done when a game is over.
	 * @param gameId
	 */
	public void removeExistingGame(int gameId) {
		_games.remove(gameId);
	}
	
	/**
	 * check if the player is currently in a game
	 * @param id the user id
	 * @return true if the user is in game, otherwise false
	 */
	public boolean isPlayerInGame(int id) {
		return _playersInGame.containsKey(id);
	}
	
	/**
	 * return the game in which the user is playing in
	 * @param id the user id
	 * @return the game instance in which the player is
	 */
	public GameViewModel findGameByUserId(int id) {
		Integer gameId = _playersInGame.get(id);
		if (gameId != null) {
			return _games.get(gameId);
		}
		return null;
	}
}
