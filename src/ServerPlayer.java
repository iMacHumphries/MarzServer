import java.net.InetAddress;

public class ServerPlayer {
	private String username;
	private InetAddress ip;
	private int port;

	public ServerPlayer(String _username, InetAddress _ip, int _port) {
		username = _username;
		ip = _ip;
		port = _port;
	}

	public boolean equals(ServerPlayer other) {
		return username.equalsIgnoreCase(other.username);
	}
	
	public void setIp(InetAddress _ip) {
		ip = _ip;
	}

	public void setPort(int _port) {
		port = _port;
	}

	public String getUsername() {
		return username;
	}
	
	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}
}
