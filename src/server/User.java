package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class User implements Runnable {

	private InputStream inStream;
	private Scanner scanner;
	private DatabaseFacade db;

	private List<Channel> channels;
	private LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
	private Thread consumerThread;

	private String nick;
	private String pass;
	private String user;
	private String host;

	boolean passSent;

	public User(Socket clientSocket, List<Channel> channels) throws IOException {
		this.channels = channels;
		inStream = clientSocket.getInputStream();
		scanner = new Scanner(inStream);
		db = new DatabaseFacade();

		UserOutput consumer = new UserOutput(outputQueue, clientSocket.getOutputStream());
		consumerThread = new Thread(consumer);
	}

	@Override
	public void run() {

		consumerThread.start();

		while (scanner.hasNext()) {
			String input = scanner.nextLine();
			System.out.println("client request: " + input);

			String[] tokens = input.split("\\s");
			String command = tokens[0];

			switch (command) {
			case "PASS":
				pass = tokens[1].replace(":", "");
				passSent = true;
				break;

			case "NICK":
				nick = tokens[1];

				synchronized (db) {
					if (!db.userExists(nick)) {
						db.insertUser(nick, "");
					} else if (passSent) {
						if (!db.checkPassword(nick, pass)) {
							input = "QUIT";
						}
					}
				}
				break;

			case "USER":
				user = tokens[1];
				host = tokens[3];
				outputQueue.add(":server 001" + " " + nick + " :Welcome to the eIRC server!");
				break;

			case "PING":
				outputQueue.add(":server PONG");
				break;

			case "JOIN":
				String channelName = tokens[1];

				if (!channels.contains(channelName)) {
					Channel channel = new Channel(channelName);
					channels.add(channel);
					channel.addUser(nick);
				} else {
					channels.get(channels.indexOf(channelName)).addUser(nick);
				}

				outputQueue.add(":" + userMask() + " " + input);
				break;
			}

			if (input.startsWith("QUIT")) {
				System.out.println("quitting...");
				break;
			}
		}

		System.out.println("interrupting consumer thread...");
		consumerThread.interrupt();

		try {
			System.out.println("closing inputStream...");
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String userMask() {
		return nick + "!" + user + "@" + host;
	}
}
