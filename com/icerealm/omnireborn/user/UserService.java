package com.icerealm.omnireborn.user;

import java.util.HashSet;
import java.util.Set;

/**
 * manage the user currently connected to the server
 * @author neilson
 *
 */
public class UserService {

	
	private Set<OmniUser> _currentUsers = null;
	
	/**
	 * default constructor
	 */
	public UserService() {
		_currentUsers = new HashSet<OmniUser>();
	}
	
	/**
	 * add new user
	 * @param u the user to be added
	 */
	public void addUser(OmniUser u) {
		_currentUsers.add(u);
	}
	
	/**
	 * remove an existing user
	 * @param u the user to be removed
	 */
	public void removeUser(OmniUser u) {
		_currentUsers.remove(u);
	}
	
	/**
	 * find a player that is not in game
	 * @return a user if availalb, if no player available, it return null
	 */
	public OmniUser findAvailableUser() {
		
		for (OmniUser u : _currentUsers) {
			if (!u.getInGame()) {
				return u;
			}
		}
		return null;
	}
	
	/**
	 * search for an existing user by its unique ID
	 * @param id the unit id
	 * @return the user if found, otherwise null
	 */
	public OmniUser getUser(int id) {		
		for (OmniUser u : _currentUsers) {
			if (u.getId() == id) {
				return u;
			}
		}
		return null;
	}
	
	
	
	
}
