package application;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	Main mainController;
	Socket socket;
	int serverPort;
	PrintWriter writer;
	BufferedReader reader;
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
					writer = new PrintWriter(socket.getOutputStream(), true);
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					sendString("Register");
					int id = readInt();
					System.out.println("My id is: " + id);
					Platform.runLater(() -> {
						mainController.serverConnected();
					});
				} catch(IOException e) {
					e.printStackTrace();
					mainController.closeAll();
				}
			}
		}).start();
	}

	void connectPlayer() {
		new Thread(new Runnable() {
			public void run() {
				sendString("Match");
				try {
					int reply = readInt();
					if (reply >= 0) {
						System.out.println("Connected with player " + reply);
						Platform.runLater(() -> {
							mainController.playerConnected(reply);
						});
					}
				}
				catch (IOException e) {
					System.out.println("Connection failed");
					Platform.runLater(() -> {
						mainController.closeAll();
					});
				}
			}
		}).start();
	}

	public int informServer(int playerId, int x, int y) {
		try{
			sendString("Move");
			System.out.println("I am player: " + id + " I moved on " + x + " " + y);
			sendInt(x);
			sendInt(y);
			System.out.println("Sent data: " + id + " " + x + " " + y);
			int gameState = readInt();
			Platform.runLater(() -> {
				mainController.checkWin(gameState, playerId);
			});
			return gameState;
		} catch (IOException e) {
			System.out.println("Opponent player / Server crashed");
			Platform.runLater(() -> {
				mainController.closeAll();
			});
			return -1;
		}
	}

	public void getMovement(int playerId) {
		new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Go get! oppo: " + playerId);
					sendString("Get");
					sendInt(playerId);
					int x = readInt();
					if(x == -1) {
						throw new Exception();
					}
					int y = readInt();
					int gameState = readInt();
					System.out.println("I am player: " + id + "My opponent moved on " + x + " " + y);
					Platform.runLater(() -> {
						mainController.controller.receiveMove(new Movement(x, y));
						mainController.checkWin(gameState, playerId ^ 1);
					});
				} catch (Exception e) {
					System.out.println("Opponent player / Server crashed");
					Platform.runLater(() -> {
						mainController.closeAll();
					});
				}
			}
		}).start();
	}

	int readInt() throws IOException {
		int num = Integer.parseInt(reader.readLine());
		return num;
	}

	int sendInt(int num) {
		String numStr = Integer.toString(num);
		writer.println(numStr);
		return 0;
	}

	int sendString(String message) {
		writer.println(message);
		return 0;
	}

	String readString() throws IOException {
		return reader.readLine();
	}
}
