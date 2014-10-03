package com.icerealm.omnireborn.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.icerealm.omnireborn.map.MapViewModel;
import com.icerealm.omnireborn.unit.UnitViewModel;
import com.icerealm.omnireborn.user.OmniUser;

/**
 * reprensents all the data related to a game in progress.
 * @author neilson
 *
 */
public class GameViewModel {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * the list of unit in the game
	 */
	private Set<UnitViewModel> units = null;
	
	/**
	 * the list of player in the game
	 */
	private Set<OmniUser> users = null;
	
	/**
	 * the game map, represents the board
	 */
	private MapViewModel currentMap = null;
	
	/**
	 * state of the current game
	 */
	private boolean state = false;
	
	/**
	 * a private iterator to manage the next player to play
	 */
	private Iterator<OmniUser> playerQueue = null;
	
	/**
	 * the current player that can play
	 */
	private OmniUser currentPlayer = null;
	
	/**
	 * unique game id
	 */
	private int gameId = 0;
	
	/**
	 * minimum players to keep a game alive
	 */
	private int minimumPlayers = 2;
	
	/**
	 * the number of turn completed
	 */
	private int turnDone = 0;
	
	/**
	 * indicate which player has completed their turn
	 */
	private Map<Integer, Boolean> _turnCompleted = null;
	
	/**
	 * default constructor
	 */
	public GameViewModel() {
		_turnCompleted = new HashMap<Integer, Boolean>();
	}
	
	@Override
	public String toString() {
		return "Game[" + gameId + "]" + turnDone +  "/" + users.size();
	}
	
	/**
	 * set the player list for this game. It also default the turn done indicator to false.
	 * @param users the list of users that will play
	 */
	public void setUsers(Set<OmniUser> users) {
		
		// defaulting the turn completed indicator for all user to false
		if (users != null) {
			for (OmniUser u : users) {
				_turnCompleted.put(u.getId(), false);
			}
		}
		
		// assigning the list of player
		this.users = users;
		
		// get the iterator for future use
		playerQueue = this.users.iterator();
	}
	
	/**
	 * return the next player available. this call the next() function of the users list iterator.
	 * Catches the exception and set the player null
	 * @return the next player, if end of available player, returns null.
	 */
	public OmniUser getNextActivePlayer() {
		try {
			currentPlayer = playerQueue.next();
		}
		catch (NoSuchElementException ex) {
			LOGGER.log(Level.FINE, ex.toString(), ex);
			currentPlayer = null;
		}
		
		return currentPlayer;
	}
	
	/**
	 * return the active player, it does not change it
	 * @return return the active player
	 */
	public OmniUser getActivePlayer() {
		return currentPlayer;
	}
	
	/**
	 * get a new iterator from the users list. it does not set the active players.
	 * you need to call getNextActivePlayer to active it
	 */
	public void resetPlayerQueue() {
		playerQueue = this.users.iterator();
	}
	
	/**
	 * set the same indicator for all the layer
	 * @param b
	 */
	public void setAllPlayerTurnDone(boolean b) {
		Iterator<OmniUser> iter = users.iterator();
		while (iter.hasNext()) {
			_turnCompleted.put(iter.next().getId(), b);
		}
	}
	public void setPlayerTurnDone(int p, boolean b) {
		_turnCompleted.put(p, b);
	}
	
	public Map<Integer, Boolean> getTurnCompleted() {
		return _turnCompleted;
	}
	
	public boolean isEnoughPlayer() {
		return users.size() >= minimumPlayers;
	}
	public void setMinimumPlayers(int m) {
		minimumPlayers = m;
	}
	public int getMinimumPlayers() {
		return minimumPlayers;
	}
	public Set<OmniUser> getUsers() {
		if (users == null) {
			users = new HashSet<OmniUser>();
		}
		return users;
	}
	
	public MapViewModel getCurrentMap() {
		return currentMap;
	}
	public void setCurrentMap(MapViewModel currentMap) {
		this.currentMap = currentMap;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public Set<UnitViewModel> getUnits() {
		if (units == null) {
			units = new HashSet<UnitViewModel>();
		}
		return units;
	}
	public void setUnits(Set<UnitViewModel> units) {
		this.units = units;
	}
	public int getTurnDone() {
		return turnDone;
	}
	public void setTurnDone(int turnDone) {
		this.turnDone = turnDone;
	}
	
}
