import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

public class Game extends StateDecider {

	private static long[][] r;
	public static String[] stringRep;

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
		// System.out.println(r[100][2]);
	}

	private void initializeR() {
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
		State s = new State(grid, idx, color == 1, noFilled);
		int inf = (int) 1e9;
		// Play play = chooseRandom(s);
//		Play play = Search.miniMax(s, 5, true, color);
		// Play play = Search.alphaBeta(s, 5, -inf, inf, true, color);
		 Play play = Search.IDMO(s, 5, color);

		// Play play = Search.alphaBetaNegaMax(s, 6, Integer.MIN_VALUE,
		// Integer.MAX_VALUE, true, color);
		System.out.println(toString(play.pos) + " " + play.value + " " + Search.winningstates + " " + Search.allstates);
		return play;
	}

	public static String toString(int pos) {
		return stringRep[pos];
	}

	public void handlePlay(int idx, int playerColor) {
		grid[idx] = turn ? 1 : 2;
		noFilled++;
		if (isWinning(idx))
			celebrate(playerColor);
		turn = !turn;
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
