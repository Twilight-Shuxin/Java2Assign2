package application;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	Main mainController;
	Socket socket;
	int serverPort;
	InputStream inputStream;
	OutputStream outputStream;
	byte[] buffer = new byte[1024];
	int id = 0;

	Client(Main mainController, int port) {
		this.mainController = mainController;
		serverPort = port;
	}

	void connect() {
		new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("connecting");
					socket = new Socket(InetAddress.getLocalHost(), 9999);
					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();
					sendString("Register");
					int id = readInt();
					System.out.println("My id is: " + id);
					Platform.runLater(() -> {
						mainController.serverConnected();
					});
				} catch(IOException e) {
					e.printStackTrace();
					closeAll();
					mainController.closeAll();
				}
			}
		}).start();
	}

	void closeAll() {

	}

	void connectPlayer() {
		new Thread(new Runnable() {
			public void run() {
				sendString("Match");
				int reply = readInt();
				if(reply >= 0) {
					System.out.println("Connected with player " + reply);
					Platform.runLater(() -> {
						mainController.playerConnected();
					});
				}
			}
		}).start();
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
			System.out.println("Failed to read int");
			return null;
		}
		return message;
	}

	void receiveMove() {
		int x = readInt();
		int y = readInt();
		int state = readInt();
	}

	void sendMove(int x, int y) {
		sendInt(x);
		sendInt(y);
	}
}
