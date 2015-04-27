package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class IRCServer implements Runnable {

	ServerSocket serverSocket;
	Socket clientSocket;
	Scanner scanner;

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
				
				while (true) {
					InputStream inStream = clientSocket.getInputStream();

					scanner = new Scanner(inStream);

					while (scanner.hasNext()) {
						String input = scanner.next();
						System.out.println(input);

						PrintWriter outStream = new PrintWriter(clientSocket.getOutputStream());
						outStream.println(input);
						outStream.flush();
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
