package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class User implements Runnable {

	private InputStream inStream;
	private Scanner scanner;
	private DatabaseFacade db;

	private Map<String, Channel> channels;
	private LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
	private Thread consumerThread;

	private String nick;
	private String pass;
	private String user;
	private String host;

	boolean passSent;

	public User(Socket clientSocket, Map<String, Channel> channels) throws IOException {
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
				if (tokens.length < 2) {
					outputQueue.add(":server 461 " + nick + " :No password given");
					break;
				}
				
				pass = tokens[1].replace(":", "");
				passSent = true;
				break;

			case "NICK":
				if (tokens.length < 2) {
					outputQueue.add(":server 431 :No nickname given");
					break;
				}
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
				if (tokens.length < 5) {
					outputQueue.add(":server 461 :Missing parameters");
					break;
				}
				user = tokens[1];
				host = tokens[3];
				outputQueue.add(":server 001" + " " + nick + " :Welcome to the eIRC server!");
				break;

			case "PING":
				outputQueue.add(":server PONG");
				break;

			case "JOIN":
				String channelName = tokens[1];
				Channel channel = null;

				synchronized (channels) {
					if (!channels.containsKey(channelName)) {
						System.out.println("Channel " + channelName + " doesnt exist, creating...");
						channel = new Channel(channelName);
						channels.put(channelName, channel);
						channel.addUser(this);
					} else {
						System.out.println("Adding user to existing channel " + channelName);
						channel = channels.get(channelName);
						channel.addUser(this);
					}
				}

				outputQueue.add(":" + userMask() + " " + input);

				synchronized (channels) {
					for (User user : channel.getMembers()) {
						outputQueue.add(":server 353 " + nick + " = " + channelName + " :" + user.getNick());
					}
				}

				outputQueue.add(":server 366 " + nick + " " + channelName + " :end of /NAMES list");
				break;
				
			case "PRIVMSG":
				String target = tokens[1];
				if (channels.containsKey(target)) {
					channel = channels.get(target);
					channel.sendMessage(":" + userMask() + " " + input, nick);
				}
				break;
			}

			if (input.startsWith("QUIT")) {
				System.out.println("quitting...");
				for (Map.Entry<String, Channel> entry : channels.entrySet()) {
					entry.getValue().removeUser(nick, userMask());
				}
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
	
	public void sendMessage(String message) {
		outputQueue.add(message);
	}
	
	public String getNick() {
		return nick;
	}
}
