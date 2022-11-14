package application;

import sun.awt.image.ImageWatched;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class PlayerConnection {
	List<Queue<Movement>> movementLists = new LinkedList<>();
	int[][] board = new int[3][3];
	int[] dx = {-1, 1, 0, 0, -1, 1, -1, 1};
	int[] dy = {0, 0, 1, -1, -1, 1, 1, -1};
	int gameState = 0;
	boolean failedConnection = false;

	PlayerConnection() {
		movementLists.add(new ArrayBlockingQueue<Movement>(10));
		movementLists.add(new ArrayBlockingQueue<Movement>(10));
	}

	public void addMovement(int id, Movement movement) {
		movementLists.get(id).add(movement);
		addMovementOnBoard(id, movement);
	}

	public Movement getMovement(int id) throws Exception {
		while(true) {
			if(!movementLists.get(id).isEmpty()) {
				Movement movement = movementLists.get(id).remove();
				return movement;
			}
			System.out.println(id + " Is empty...");
			Thread.sleep(500);
			if(failedConnection)
				break;
		}
		throw new Exception();
	}

	public int checkBoard() {
		for(int k = 1; k <= 2; k ++)
			for(int i = 0; i < 3; i ++)
				for(int j = 0; j < 3; j ++) {
					for(int p = 0; p < 8; p ++) {
						int mx = i, my = j;
						int cnt = 0;
						while (mx >= 0 && mx < 3 && my >= 0 && my < 3) {
							cnt += (board[mx][my] == k) ? 1 : 0;
							mx += dx[p];
							my += dy[p];
						}
						mx = i;
						my = j;
						while (mx >= 0 && mx < 3 && my >= 0 && my < 3) {
							cnt += (board[mx][my] == k) ? 1 : 0;
							mx -= dx[p];
							my -= dy[p];
						}
						cnt -= (board[i][j] == k) ? 1 : 0;
						if(cnt == 3) {
							return k;
						}
					}
				}
		int cnt = 0;
		for(int i = 0; i < 3; i ++)
			for(int j = 0; j < 3; j ++) {
				if(board[i][j] == 0) {
					cnt ++;
				}
			}
		if(cnt == 0) return 3;
		return 0;
	}

	public synchronized void addMovementOnBoard(int id, Movement movement) {
		board[movement.x][movement.y] = id + 1;
		gameState = checkBoard();
	}

	public int getGameState() {
		return gameState;
	}
}
