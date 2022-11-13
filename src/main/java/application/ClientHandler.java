package application;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
	Server server;
	Socket client;
	PrintWriter writer;
	BufferedReader reader;
	PlayerConnection playerConnection;
	int id;
	int oppoPlayerId;
	byte[] buffer = new byte[1024];

	ClientHandler(Server server, Socket client) throws IOException {
		this.server = server;
		this.client = client;
		writer = new PrintWriter(client.getOutputStream(), true);
		reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
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

	void setPlayerConnection(PlayerConnection playerConnection) {
		this.playerConnection = playerConnection;
	}

	@Override
	public void run() {
		Scanner input = new Scanner(System.in);
		boolean runFlag = true;
		while(runFlag) {
			try {
				String message = readString();
				System.out.println("Received message from client " + message);
				if (message.equals("Register")) {
					System.out.println("Client request Id");
					id = server.register();
					sendInt(id);
				} else if (message.equals("Match")) {
					try {
						System.out.println("Match Id for client");
						oppoPlayerId = server.match(this, id);
						sendInt(oppoPlayerId);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (message.equals("Move")) {
					int x = readInt();
					System.out.println("x = " + x);
					int y = readInt();
					System.out.println("y = " + y);
					playerConnection.addMovement(oppoPlayerId ^ 1, new Movement(x, y));
					sendInt(playerConnection.gameState);
					System.out.println("Successfully added");
				} else {
					int id = readInt();
					Movement movement = null;
					System.out.println("Please get movement of player " + id);
					try {
						movement = playerConnection.getMovement(id);
						sendInt(movement.x);
						sendInt(movement.y);
						sendInt(playerConnection.gameState);
						// After a long time cannot get movement: end game, clean up
					} catch (Exception e) {
						sendInt(-1);
					}
				}
			} catch (IOException e) {
				runFlag = false;
			}
		}
		closeClientHandler();
	}

	void closeClientHandler() {
		server.closedPlayer(id);
	}
}
