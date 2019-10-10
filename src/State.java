import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class State extends StateDecider {
	private ArrayList<State> successors;

	// last played attributes Eh el7aga elly etla3abet 3shan awsal hena
	int idx;

	public State(int[] grid, int idx, boolean turn, int noFilled) {
		this.grid = grid;
		this.idx = idx;
		this.turn = turn;
		this.noFilled = noFilled;
	}

	public boolean isWinning() {
		return isWinning(idx);
	}

	public ArrayList<State> getSuccessors() {
		if (successors != null)
			return successors;
		int color = notColorFlag(this.grid[this.idx]);
		successors = new ArrayList<>();
		for (int i = 0; i < CELLS; i++) {
			if (isValid(i)) {
				int[] newGrid = play(grid, i, color);
				State succ = new State(newGrid, i, color == 1, this.noFilled + 1);
				successors.add(succ);
			}
		}
		return successors;
	}
	
	public ArrayList<State> getSuccessorsSorted(Play[] moves) {
		if (successors != null)
			return successors;
		int color = notColorFlag(this.grid[this.idx]);
		successors = new ArrayList<>();
		for (int i = 0; i < CELLS; i++) {
			if (isValid(i)) {
				int[] newGrid = play(grid, i, color);
				State succ = new State(newGrid, i, color == 1, this.noFilled + 1);
				successors.add(succ);
			}
		}
		successors = sortSuccessors(moves);
		return successors;
	}

	private ArrayList<State> sortSuccessors(Play[] moves) {
		return null;
	}

	private int[] play(int[] grid, int idx, int colorFlag) {
		int[] newGrid = new int[CELLS];
		for (int i = 0; i < CELLS; i++) {
			newGrid[i] = grid[i];
		}
		newGrid[idx] = colorFlag; // already played game before switching turn
		return newGrid;
	}

	private int notColorFlag(int colorFlag) {
		if (colorFlag == 1)
			return 2;
		else
			return 1;
	}

	public int countComponent(int move) { // BFS
		boolean[] visited = new boolean[CELLS];
		visited[move] = true;
		int color = grid[move];
		Queue<Integer> q = new LinkedList<Integer>();
		q.add(move);
		int c = 1;
		while (!q.isEmpty()) {
			int next = q.poll();
			Pair[] neighbors = getNearestSix(next);
			for (Pair pair : neighbors) {
				if (!visited[pair.idx] && grid[pair.idx] == color) {
					visited[pair.idx] = true;
					q.add(pair.idx);
					c++;
				}
			}
		}
		return c;
	}	
}
