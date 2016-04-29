
public interface ServerListener {
	
	/**
	 * 	Called when the server wants to display info
	 * 
	 * @parm msg
	 */
	void serverLog(String msg);
	
	/**
	 * Called when the server receives a login packet.
	 * 
	 * @param player
	 */
	void serverDidAddPlayer(ServerPlayer player);
	
	/**
	 * Called when a server is already running on the 
	 * specific ip.
	 */
	void serverAlreadyRunning();
	
	/**
	 * Called when a player is removed from the server.
	 * Could be from player disconnect.
	 * 
	 * @param player
	 */
	void serverDidRemovePlayer(ServerPlayer player);
	
	/**
	 * Called when server receives a message
	 * 
	 * @param chatPacket
	 */
	void serverDidRecieveData(byte[] data);
}
