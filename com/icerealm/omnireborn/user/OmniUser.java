package com.icerealm.omnireborn.user;

import java.util.logging.Logger;

/**
 * Represents a unique player on the server
 * @author neilson
 *
 */
public class OmniUser  {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	private static int USER_ID = 0;
	
	/**
	 * unique id of this user
	 */
	private int _id = 0;
	
	/**
	 * represents the unique name of this user
	 */
	private String _name = null;
	
	/**
	 * reprenset if the user is in game
	 */
	private boolean _inGame = false;
	
	/**
	 * default constructor, the name must be unique
	 * @param name
	 */
	public OmniUser(String name) {
		_name = name;
		_id = USER_ID++;
		_inGame = false;
	}
	
	/**
	 * set this user as in-game
	 * @param b true if in-game, otherwise false
	 */
	public void setInGame(boolean b) {
		_inGame = b;
	}
	
	/**
	 * reutrn the in-game status of this usre
	 * @return true if in game, otherwise false
	 */
	public boolean getInGame() {
		return _inGame;
	}
	
	/**
	 * return the user unique id
	 * @return the user unique id
	 */
	public int getId() {
		return _id;
	}
	
	/**
	 * Return the user's name
	 * @return
	 */
	public String getName() {
		return _name;
	}
	
	@Override 
	public int hashCode() {
		return _id;
	}
	
	 @Override
	 public String toString() {
		 return _name;
	 }
	 
	 @Override
	 public boolean equals(Object other) {
		 return (other instanceof OmniUser) &&
				((OmniUser)other).getId() == _id; 
	 }
}
