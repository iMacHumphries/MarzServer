import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import server.Constants;
import server.packets.Packet;
import server.packets.Packet00Login;
import server.packets.Packet01Disconnect;
import server.packets.Packet03ServerData;
import server.packets.PacketType;

/**
 * Server.java - Thread to listen for packets
 * @author Ben Humphries
 * @version Mar 26, 2016
 */
public class Server extends Thread {

	private DatagramSocket socket;                    // Server socket
	private ArrayList<ServerPlayer> connectedPlayers; // List of server players online
	
	private ServerListener delegate;                  // Delegate work to others
	
	public Server(ServerListener delegate, int port) {
		this.delegate = delegate;
		
		connectedPlayers = new ArrayList<>();
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			delegate.serverAlreadyRunning();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[Constants.MAX_BYTES];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			parsePacket(packet);
		}
	}
	
	private void parsePacket(DatagramPacket pack) {
		byte[] data = pack.getData();
		InetAddress ip = pack.getAddress();
		int port = pack.getPort();
		String message = new String(data);
		String id = message.substring(0, 2);

		delegate.serverDidRecieveData(data);
		
		PacketType type = PacketType.getTypeForId(id);
		switch (type) {

		case INVALID:
			delegate.serverLog("Invalid packet recieved.");
			break;

		case LOGIN:
			Packet00Login loginPack = new Packet00Login(data);
			ServerPlayer player = new ServerPlayer(loginPack.getUsername(), ip, port);
			addConnection(player, loginPack);
			delegate.serverDidAddPlayer(player);
			break;

		case DISCONNECT:
			removeConnection(new Packet01Disconnect(data));
			break;

		case ALL_DATA:
			sendDataToAllClients(data); // Quick pass!
			break;

		case SERVER_DATA:
			Packet03ServerData serverPack = new Packet03ServerData(data); 
			// Look at stuff here

			break;

		default:
			delegate.serverLog("[WARNING] Forgot to add PacketType to server parse method!");
			break;
		}
		
	}
	
	public void addConnection(ServerPlayer player, Packet00Login loginPack) {
		boolean alreadyConnected = false;

		// Go through all players
		for (ServerPlayer p : connectedPlayers) {
			// Check if p has connected before
			if (p.equals(player)) {
				alreadyConnected = true;
				// Check if there is data for that player
				if (p.getIp() == null)
					p.setIp(player.getIp());
				if (p.getPort() == -1)
					p.setPort(player.getPort());
			} else {
				// Haven't connected.
				// Sending the new player login to every connected player one at at time
				sendData(loginPack.getData(), p.getIp(), p.getPort());

				// Sending every already connected player to the new player
				Packet00Login loginP = new Packet00Login(p.getUsername());
				sendData(loginP.getData(), player.getIp(), player.getPort());
			}
		} // End of for loop.

		if (!alreadyConnected) {
			connectedPlayers.add(player);
		}
	}
	
	public void removeConnection(Packet01Disconnect disconnectPack) {
		// Search for the disconnecting player
		ServerPlayer disconnectingPlayer = getPlayerWithName(disconnectPack.getUsername());

		// Remove the player from connected players
		if (disconnectingPlayer != null) {
			connectedPlayers.remove(disconnectingPlayer);
			delegate.serverDidRemovePlayer(disconnectingPlayer);
		}

		// Send the disconnect to every client
		this.sendDataToAllClients(disconnectPack.getData());
	}
	
	public ServerPlayer getPlayerWithName(String name) {
		ServerPlayer result = null;
		for (ServerPlayer player : connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(name)) {
				result = player;
				break;
			}
		}
		return result;
	}
	
	public void sendData(byte[] data, InetAddress ip, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendDataToAllClients(byte[] data) {
		for (ServerPlayer player : connectedPlayers) {
			sendData(data, player.getIp(), player.getPort());
		}
	}
	
	public ArrayList<ServerPlayer> getConnectedPlayers() {
		return connectedPlayers;
	}
}
