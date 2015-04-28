package server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class UserOutputQueueConsumer implements Runnable {

	private LinkedBlockingQueue<String> queue;
	private OutputStream outputStream;
	
	public UserOutputQueueConsumer(LinkedBlockingQueue<String> queue, OutputStream outputStream) {
		this.queue = queue;
		this.outputStream = outputStream;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				String output = queue.take();
				output += "\r\n";
				System.out.print("sending response: " + output);
				outputStream.write(output.getBytes());
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
