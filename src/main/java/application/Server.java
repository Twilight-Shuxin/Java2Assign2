package application;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
	ServerSocket serverSocket;
	Queue<Integer> idQueue = new LinkedList<>();
	Queue<Integer> playerList = new LinkedList<>();
	Map<Integer, Integer> matchedMap = new HashMap<>();
	int maxId = 0;

	Server(int portNum) throws IOException {
		serverSocket = new ServerSocket(9999);
		serverSocket.setReuseAddress(true);
		while (true) {
			Socket client = serverSocket.accept();
			ClientHandler clientSock = new ClientHandler(this, client);
			new Thread(clientSock).start();
		}
	}

	public int getId() {
		int id;
		if(idQueue.isEmpty()) {
			id = maxId;
			maxId += 1;
		}
		else {
			id = idQueue.remove();
		}
		return id;
	}

	public int managerRegister() {
		int id = getId();
		playerList.add(id);
		return id;
	}

	public int managerMatch(int id) {
		if(matchedMap.getOrDefault(id, -1) != -1) {
			return matchedMap.get(id);
		}
		for(Iterator<Integer> it = playerList.iterator(); it.hasNext(); ) {
			int player = it.next();
			if(player == id)
				continue;
			if(matchedMap.getOrDefault(player, -1) == -1) {
				matchedMap.put(player, id);
				matchedMap.put(id, player);
				return player;
			}
			else {
				it.remove();
			}
		}
		return -1;
	}

	public synchronized int listManager(int opt, int para1) {
		if(opt == 0) {
			int id = managerRegister();
			return id;
		}
		else if(opt == 1) {
			int player = managerMatch(para1);
			return player;
		}
		else return -2;
	}

	public int register() {
		int id = listManager(0, 0);
		return id;
	}

	public int match(int id) throws InterruptedException {
		boolean found = false;
		int player;
		while(true) {
			player = listManager(1, id);
			System.out.println("matched player " + player + " ....");
			if(player != -1) {
				break;
			}
			Thread.sleep(2000);
		}
		return player;
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server(9999);
	}
}