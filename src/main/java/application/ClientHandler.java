package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
	Server server;
	Socket client;
	InputStream inputStream;
	OutputStream outputStream;
	int id;
	int oppoPlayerId;
	byte[] buffer = new byte[1024];

	ClientHandler(Server server, Socket client) throws IOException {
		this.server = server;
		this.client = client;
		inputStream = client.getInputStream();
		outputStream = client.getOutputStream();
	}

	public void write(String message) throws IOException {
		outputStream.write(message.getBytes());
	}

	int readInt() {
		int num;
		try {
			int len = inputStream.read(buffer);
			num = Integer.parseInt(new String(buffer, 0, len));
		} catch (IOException e) {
			System.out.println("Failed to read int");
			return -1;
		}
		return num;
	}

	int sendInt(int num) {
		String numStr = Integer.toString(num);
		try {
			outputStream.write(numStr.getBytes());
		} catch (IOException e) {
			System.out.println("Failed to send int");
			return 1;
		}
		return 0;
	}

	int sendString(String message) {
		try {
			outputStream.write(message.getBytes());
		} catch (IOException e) {
			System.out.println("Failed to send string");
			return 1;
		}
		return 0;
	}

	String readString() {
		String message;
		try {
			int len = inputStream.read(buffer);
			message = new String(buffer, 0, len);
		} catch (IOException e) {
			System.out.println("Failed to read string");
			return null;
		}
		return message;
	}

	@Override
	public void run() {
		Scanner input = new Scanner(System.in);
		while(true) {
			String message = readString();
			System.out.println("Received message from client " + message);
			if(message.equals("Register")) {
				System.out.println("Client request Id");
				id = server.register();
				sendInt(id);
			}
			else if(message.equals("Match")) {
				try {
					System.out.println("Match Id for client");
					oppoPlayerId = server.match(id);
					sendInt(oppoPlayerId);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
