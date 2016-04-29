import server.Constants;


public class ServerController implements ServerListener {

	private Server server;
	private ServerView view;
	
	public ServerController() {
		view = new ServerView();
		
		server = new Server(this, Constants.PORT_NUMBER);
		server.start();
	}

	@Override
	public void serverDidAddPlayer(ServerPlayer player) {
		view.appendStringToLog(player.getUsername() + " has just logged in.");
		view.displayPlayers(server.getConnectedPlayers());
	}

	@Override
	public void serverAlreadyRunning() {
		view.appendStringToLog("Server is already running on this machine.");
	}

	@Override
	public void serverDidRemovePlayer(ServerPlayer player) {
		view.appendStringToLog(player.getUsername() + " has disconnected.");
		view.displayPlayers(server.getConnectedPlayers());
	}

	@Override
	public void serverDidRecieveData(byte[] data) {
		view.appendStringToLog("Server received data: " + data);
	}

	@Override
	public void serverLog(String msg) {
		view.appendStringToLog(" [SERVER LOG] " + msg);
	}
}
