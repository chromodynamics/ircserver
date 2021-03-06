package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class IRCServer implements Runnable {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Map<String, Channel> channels = new HashMap<>();
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(6667);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			System.out.println("Waiting for connection");

			try {
				clientSocket = serverSocket.accept();
				System.out.println("Got connection");

				System.out.println("Spawning user thread");
				User user = new User(clientSocket, channels);
				Thread userThread = new Thread(user);
				userThread.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		IRCServer server = new IRCServer();
		Thread thread = new Thread(server);
		thread.start();
	}
}
