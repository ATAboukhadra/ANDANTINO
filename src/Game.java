import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class Game extends StateDecider {

	public static long[][] r;
	public static String[] stringRep;
	public static long initialHash;
	public static double startTime;
	public static int inf = 1000;

	public Game() {
		xs = new int[CELLS];
		ys = new int[CELLS];
		turn = false; // white turn
		grid = new int[CELLS];
		grid[CELLS / 2] = 1; // black cell is played
		noFilled = 1;
		neighbors = new Pair[CELLS][];
		r = new long[CELLS][3];
		stringRep = new String[CELLS];
		initializeR();
		initialHash = r[CELLS / 2][1];
		// System.out.println(r[100][2]);
	}

	private void initializeR() {
		int x = -5;
		while (x == 5)
			;

		HashSet<Long> used = new HashSet<>();
		Random random = new Random();
		int i = 0;
		int j = 0;
		while (used.size() < CELLS * 3) {
			long value = random.nextLong();
			if (!used.contains(value)) {
				used.add(value);
				r[i][j] = value;
				j++;
				if (j == 3) {
					i++;
					j = 0;
				}
			}
		}
	}

	public int findNearestCenter(int x, int y) {
		double min = Double.MAX_VALUE;
		int idx = -1;
		for (int i = 0; i < CELLS; i++) {
			double dist = dist(x, y, xs[i], ys[i]);
			if (dist < min) {
				min = dist;
				idx = i;
			}
		}
		return idx;
	}

	public Play search(int idx, int color) {
		State s = new State(grid, idx, color == 1, noFilled, initialHash);
		// Search.originalDepth = 5;
		// Play play = chooseRandom(s);
		// Play play = Search.miniMax(s, 5, true, color);
		// Play play = Search.alphaBeta(s, 5, -inf, inf, true, color);
		// Play play = Search.alphaBetaNegaMax(s, 6, Integer.MIN_VALUE,
		// Integer.MAX_VALUE, true, color);

		// Play play = Search.idmo(s, 9, color); // Not stable as much as TT and it
		// needs something to stop the search when time's up
		// Play play = Search.alphaBetaTT(s, 6, -inf, inf, true, color);
		// Play play = Search.alphaBetaIDTT(s, 11, color);
//		Play play = Search.alphaBetaTTKillerMoves(s, 6, -inf, inf, true, color);
		 Play play = Search.alphaBetaIDTTKillerMoves(s, 11, color);

		System.out.println(
				"Best Move: " + toString(play.pos) + " --- Score = " + play.value + " --- # of Winning States = "
						+ Search.winningStates + " --- # of Searched States = " + Search.allStates);
		System.out.println("------");
		return play;
	}

	public static String toString(int pos) {
		return stringRep[pos];
	}

	public void handlePlay(int idx, int playerColor) {
		grid[idx] = turn ? 1 : 2;
		noFilled++;
		Main.history.add(idx);
		if (isWinning(idx))
			celebrate(playerColor);
		if (isLosing(idx))
			celebrate(playerColor == 1 ? 2 : 1);
		turn = !turn;
	}

	private boolean isLosing(int idx) {
		boolean[] visited = new boolean[CELLS];
		visited[idx] = true;
		int color = grid[idx];
		Queue<Integer> q = new LinkedList<Integer>();
		q.add(idx);
		while (!q.isEmpty()) {
			int next = q.poll();
			Pair[] neighbors = getNearestSix(next);
			if (neighbors.length < 6)
				return false;
			for (Pair pair : neighbors) {
				if (!visited[pair.idx] && (grid[pair.idx] == color || grid[pair.idx] == 0)) {
					visited[pair.idx] = true;
					q.add(pair.idx);
				}
			}
		}
		return true;
	}

	public void celebrate(int color) {
		Main.gameover = true;
		String win;
		if (color == 1) {
			win = "BLACK WINS!";
			System.out.println(win);
		} else {
			win = "WHITE WINS!";
			System.out.println(win);
		}
		double currentTime = System.currentTimeMillis();
		int time = (int) ((currentTime - startTime) / 1000);
		System.out.println("# of Total moves are = " + this.noFilled + " in " + time + " seconds");
		Main.switchToWinning(win);

	}

	public static Play chooseRandom(State s) {
		ArrayList<State> childs = s.getSuccessors();
		Random r = new Random();
		int random = r.nextInt(childs.size());
		return new Play(0, childs.get(random).idx);
	}

	public void createNeighbors() {

		for (int i = 0; i < CELLS; i++) {
			int x = xs[i];
			int y = ys[i];
			neighbors[i] = nearestSix(x, y);
			neighbors[i] = removeExtra(neighbors[i]);
		}
	}

	private Pair[] removeExtra(Pair[] pairs) {
		ArrayList<Pair> outList = new ArrayList<>();
		for (int i = 0; i < pairs.length; i++) {
			if (pairs[i].dist <= expectedDist) {
				outList.add(new Pair(pairs[i].dist, pairs[i].idx));
			}
		}
		Pair[] out = new Pair[outList.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = outList.get(i);
		}
		return out;
	}

	public Pair[] nearestSix(int x, int y) {
		PriorityQueue<Pair> pq = new PriorityQueue<>();
		for (int i = 0; i < CELLS; i++) {
			int dist = (int) dist(x, y, xs[i], ys[i]);
			pq.add(new Pair(dist, i));
		}

		Pair[] output = new Pair[6];
		pq.poll();
		for (int i = 0; i < 6; i++) {
			output[i] = pq.poll();
		}

		return output;
	}

}
