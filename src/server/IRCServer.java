package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class IRCServer implements Runnable {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Scanner scanner;
	private ArrayList<Channel> channels = new ArrayList<>();

	private static final String RPL_WELCOME = "001"; 

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

				InputStream inStream = clientSocket.getInputStream();
				PrintWriter outStream = new PrintWriter(clientSocket.getOutputStream());
				scanner = new Scanner(inStream);
				String nick = null;
				
				while (true) {
					while (scanner.hasNext()) {
						String input = scanner.nextLine();
						System.out.println(input);

						if (input.startsWith("NICK")) {
							nick = input.split("\\s")[1];
							
							outStream.println(RPL_WELCOME + " " + nick);
							outStream.flush();
						}
						
						if (input.startsWith("QUIT")) {
							outStream.println("ERROR");
							outStream.flush();
						}
						
						if (input.startsWith("JOIN")) {
							String channelName = input.split("\\s")[1];
							
							System.out.println(channelName);
							
							if (!channels.contains(channelName)) {
								Channel channel = new Channel(channelName, "");
								channels.add(channel);
								channel.addUser(nick);
							} else {
								channels.get(channels.indexOf(channelName)).addUser(nick);
							}
							
							outStream.println(":eith!kvirc@127.0.0.1" + " " + input);
							outStream.println(":localhost 331" + " " + channelName + " :no topic");
							outStream.println(":localhost 353" + " " + channelName + " :eith!kvirc@127.0.0.1");
							outStream.println(":localhost 366" + " " + channelName + " :END OF NAMES");
//							outStream.println(channels.get(channels.indexOf(channelName)).getTopic());
							outStream.flush();
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				
			} finally {
				try {
					serverSocket.close();
					scanner.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		IRCServer server = new IRCServer();
		Thread thread = new Thread(server);
		thread.start();
	}
}
