package server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class UserOutput implements Runnable {

	private LinkedBlockingQueue<String> queue;
	private OutputStream outputStream;

	public UserOutput(LinkedBlockingQueue<String> queue, OutputStream outputStream) {
		this.queue = queue;
		this.outputStream = outputStream;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String output = queue.take();
				output += "\r\n";
				System.out.print("server response: " + output);
				outputStream.write(output.getBytes());
				
			} catch (InterruptedException e) {
				System.out.println("consumer thread interrupted, shutting down...");
				break;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			System.out.println("closing output stream...");
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
