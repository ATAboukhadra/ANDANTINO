import java.util.ArrayList;

public class State extends StateDecider {
	private ArrayList<State> successors;

	// last played attributes Eh el7aga elly etla3abet 3shan awsal hena
	int idx;
	long hashCode;
	public State(int[] grid, int idx, boolean turn, int noFilled, long hashCode) {
		this.grid = grid;
		this.idx = idx;
		this.turn = turn;
		this.noFilled = noFilled;
		this.hashCode = hashCode;
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
				long newHashCode = this.hashCode ^ Game.r[i][0]^ Game.r[i][newGrid[i]] ;
				State succ = new State(newGrid, i, color == 1, this.noFilled + 1, newHashCode);
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
				long newHashCode = this.hashCode ^ Game.r[i][0]^ Game.r[i][newGrid[i]] ;
				State succ = new State(newGrid, i, color == 1, this.noFilled + 1, newHashCode);
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

	public long getHashCode() {
		long zobrist = 0l;
		for (int i = 0; i < grid.length; i++) {
			zobrist ^= Game.r[i][grid[i]];
		}
		return zobrist;
	}
}
