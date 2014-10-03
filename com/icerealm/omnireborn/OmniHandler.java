package com.icerealm.omnireborn;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.icerealm.omnireborn.controller.ActionController;
import com.icerealm.omnireborn.user.OmniUser;
import com.icerealm.server.interaction.MultiUserHandler;
import com.icerealm.server.socket.ManagedWebSocketHandler;
import com.icerealm.server.socket.PureWebSocketHandler;
import com.icerealm.server.socket.WebSocket;

/**
 * Represents the lower level of interaction between the servers and the connected users.
 * It handles the message received by different user, the new connection and the connection
 * termination. The WebSocket class should not be used beyond this point. Work with the lists 
 * of users provided with the MultiUserHandler. 
 * @author neilson
 *
 */
public class OmniHandler extends PureWebSocketHandler {

	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * Default JsonParser
	 */
	private JsonParser _jsonParser = null;
	
	/**
	 * Manage the list of users and send message through 
	 * their corresponding WebSocket
	 */
	private MultiUserHandler<OmniUser> _usersHandler = null;
	
	/**
	 * Manage the flow between this handler and the underlying
	 * game component
	 */
	private ActionController _controller = null;
	
	/**
	 * default constructor, initialize the private class members
	 */
	public OmniHandler() {
		_jsonParser = new JsonParser();
		_usersHandler = new MultiUserHandler<OmniUser>();
		_controller = new ActionController();
		LOGGER.info("OmniReborn Handler initialized");
	}
	
	@Override
	public void onConnectionEnded(WebSocket arg0) throws Exception {
		// get the user 
		OmniUser user = _usersHandler.getUserFromWebSocket(arg0);
		
		// we receive the ouptut from the controller and send it to the players
		Map<OmniUser, JsonObject> result = _controller.userDisconnected(user);
		this.sendDataToRecipient(result);
		
		// we are done with this user, his life is done here
		_usersHandler.removeUser(arg0);
		
		// log this
		LOGGER.info(user.getId() +	" with key " + arg0.getKey() +	" connection ended");
	}
	
	@Override
	public void onMessageReceived(String msg, WebSocket ws) throws Exception {
				
		// parse the json object
		JsonObject inputElement  = _jsonParser.parse(msg).getAsJsonObject();
		
		// get the output from the input received
		Map<OmniUser, JsonObject> result = _controller.getOutputResult(
												inputElement, 
												_usersHandler.getUserFromWebSocket(ws),
												_usersHandler.getAllUsers());
		this.sendDataToRecipient(result);
	}

	@Override
	public void onNewConnection(WebSocket ws) throws Exception {
		OmniUser user = _controller.createNewUser();
		_usersHandler.addUser(ws, user);
		LOGGER.info(user.getId() + " new connection with key " + ws.getKey());
	}
	
	private void sendDataToRecipient(Map<OmniUser, JsonObject> recipients) {
		// send the message to each player
		for (OmniUser u : recipients.keySet()) {
			_usersHandler.sendToClient(u, recipients.get(u).toString());
		}
	}
}
