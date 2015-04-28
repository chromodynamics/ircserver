package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class User implements Runnable {

	private InputStream inStream;
	private Scanner scanner;

	private ArrayList<Channel> channels;
	private LinkedBlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
	private Thread consumerThread;

	private String nick;
	private String user;
	private String host;

	public User(Socket clientSocket, ArrayList<Channel> channels) throws IOException {
		this.channels = channels;
		inStream = clientSocket.getInputStream();
		scanner = new Scanner(inStream);

		UserOutputQueueConsumer consumer = new UserOutputQueueConsumer(outputQueue, clientSocket.getOutputStream());
		consumerThread = new Thread(consumer);
	}

	@Override
	public void run() {

		consumerThread.start();

		while (scanner.hasNext()) {
			String input = scanner.nextLine();
			System.out.println("client request: " + input);

			if (input.startsWith("NICK")) {
				nick = input.split("\\s")[1];
			}

			if (input.startsWith("USER")) {
				String[] tokens = input.split("\\s");
				user = tokens[1];
				host = tokens[3];

				outputQueue.add(":server 001" + " " + nick + " :Welcome to the eIRC server!");
			}

			if (input.startsWith("QUIT")) {
				System.out.println("quitting...");
				break;
			}

			if (input.startsWith("PING")) {
				outputQueue.add(":server PONG");
			}

			if (input.startsWith("JOIN")) {
				String channelName = input.split("\\s")[1];

				if (!channels.contains(channelName)) {
					Channel channel = new Channel(channelName);
					channels.add(channel);
					channel.addUser(nick);
				} else {
					channels.get(channels.indexOf(channelName)).addUser(nick);
				}

				outputQueue.add(":" + userMask() + " " + input);
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

		return;
	}

	private String userMask() {
		return nick + "!" + user + "@" + host;
	}
}
