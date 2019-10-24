import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class Evaluator {
	static boolean[] visited;
	public static final int CELLS = 271;
	static boolean[] lineVisited;

	public static int evaluate(State s, boolean winning, int colorFlag, int depth, int originalDepth) {
		int lastColor = s.grid[s.idx];
		boolean iWin = lastColor == colorFlag;
		// boolean iWin = Max;
		int actualDepth = (originalDepth - depth);
		if (winning) {
			return (iWin ? 500 - actualDepth : -500 + actualDepth); // this is done to drive the agent to win quickly or
																	// stall losing
		} else {
			int value = 0;
			// am I going to make a full row?
			value += s.countConsecutive(s.idx) * (iWin ? 6 : -6);
			// am I having different 3 in a row that can be connected to a 4 with one piece
			value += fiveInARowComponents(s, s.idx) * (iWin ? 4 : -4);
			// if I put my piece next to my other pieces, I can't lose from trap, I may lose
			// from enemies consecutives or win with my consecutives
			// I care about putting my pieces together (defensive mechanism), I don't care a
			// lot about enemies pieces
			value += countComponent(s, s.idx) * (iWin ? 4 : -1);
			// let's count how many pieces am I surrounding my enemy with
			value += enclosementValue(s, s.idx) * (iWin ? 3 : -3);
			// Knot Value, the problem is that is considers all the chain
			value += chainValueBFS(s, s.idx) * (iWin ? 5 : -5); // minimize distance
			return value;
		}
	}

	public static int fiveInARowComponents(State s, int idx) {
		int color = s.grid[idx];
		int x = StateDecider.xs[idx];
		int y = StateDecider.ys[idx];

		Pair[] neighbors = s.getNearestSix(idx);
		int line = 0;
		for (Pair p : neighbors) {
			int nx = StateDecider.xs[p.idx];
			int ny = StateDecider.ys[p.idx];
			if (color == s.grid[p.idx]) {
				lineVisited = new boolean[CELLS];
				int dir = (ny - y) / (nx - x);
				lineVisited[idx] = true;
				int line1 = s.checkLine(p.idx, dir, nx, ny, 2);
				int line2 = s.checkLine(idx, dir, x, y, 0);
				line += line1 + line2; // summing instead of maximizing ? 
			}
		}
		return line;
	}
	
	public static int chainValueBFS(State s, int move) {
		boolean[] visited = new boolean[CELLS];
		visited[move] = true;
		int color = s.grid[move];
		Queue<ChainNode> q = new LinkedList<ChainNode>();
		ChainNode cn = new ChainNode(0, 0, move);
		q.add(cn);

		ArrayList<ChainNode> tips = new ArrayList<>();
		while (!q.isEmpty()) {
			ChainNode top = q.poll();
			int idx = top.idx;
			Pair[] neighbors = s.getNearestSix(idx);
			for (Pair p : neighbors) {
				if (!visited[p.idx] && s.grid[p.idx] == color) {
					visited[p.idx] = true;

					int dist = (int) s.dist(StateDecider.xs[move], StateDecider.ys[move], StateDecider.xs[p.idx],
							StateDecider.ys[p.idx]);
					cn = new ChainNode(top.depth + 1, dist, p.idx);
					q.add(cn);
					tips.add(cn);
				}
			}
		}

		Collections.sort(tips);
		if (tips.size() == 0)
			return 0;
		return tips.get(0).chainValue;
	}

	public static int enclosementValue(State s, int move) {
		int searchfor;
		if (s.turn)
			searchfor = 2;
		else
			searchfor = 1;
		Pair[] neighbors = s.getNearestSix(move);
		int best = 0;
		for (int i = 0; i < neighbors.length; i++) {
			visited = new boolean[CELLS];
			int nidx = neighbors[i].idx;
			if (!visited[nidx] && (s.grid[nidx] == searchfor || s.grid[nidx] == 0))
				best = Math.max(best, trappedBFS(s, nidx, searchfor));
		}

		return best;
	}

	private static int trappedBFS(State s, int i, int searchfor) {
		visited = new boolean[CELLS];
		visited[i] = true;
		Queue<Pair> q = new LinkedList<Pair>();
		q.add(new Pair(0, i));
		int count = 0;
		while (!q.isEmpty() && q.peek().dist < 3) {
			Pair top = q.poll();
			int idx = top.idx;
			Pair[] neighbors = s.getNearestSix(idx);
			for (Pair p : neighbors) {
				if (!visited[p.idx]) {
					visited[p.idx] = true;
					if (s.grid[p.idx] != 0 && s.grid[p.idx] != searchfor) {
						count++;
					}
					q.add(new Pair(top.dist + 1, p.idx));
				}
			}
		}
		return count;
	}

	public static int countComponent(State s, int move) { // BFS
		boolean[] visited = new boolean[CELLS];
		visited[move] = true;
		int color = s.grid[move];
		Queue<Integer> q = new LinkedList<Integer>();
		q.add(move);
		int c = 1;
		while (!q.isEmpty()) {
			int next = q.poll();
			Pair[] neighbors = s.getNearestSix(next);
			for (Pair pair : neighbors) {
				if (!visited[pair.idx] && s.grid[pair.idx] == color) {
					visited[pair.idx] = true;
					q.add(pair.idx);
					c++;
				}
			}
		}
		return c;
	}

	static class ChainNode implements Comparable<ChainNode> {
		int depth;
		int dist;
		int idx;
		int chainValue;

		public ChainNode(int depth, int dist, int idx) {
			this.depth = depth;
			this.dist = dist;
			this.idx = idx;
			if (dist != 0)
				chainValue = depth * 100 / dist;
			else
				chainValue = 0;
		}

		@Override
		public int compareTo(ChainNode o) {
			return o.chainValue - this.chainValue;
		}

		public String toString() {
			return Game.toString(this.idx) + " " + depth + " " + dist + " " + chainValue;
		}

	}

}
