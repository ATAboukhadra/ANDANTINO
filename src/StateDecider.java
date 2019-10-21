import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class StateDecider {

	int[] grid; // 1 -> black ---- 2 -> white
	public boolean turn; // true -> black ---- false -> white
	int noFilled;

	static int[] xs;
	static int[] ys;
	static final int CELLS = 271;

	static final int XS = 225;
	static final int YS = 76;
	static final int W = 38;
	static final int H = 19;
	static final double XDIST = 19.72;
	static final double YDIST = 34.2;

	static boolean[] visited;
	static final int expectedDist = 45;
	static boolean[] lineVisited;

	static Pair[][] neighbors;

	static int opponentCount;

	public boolean isValid(int idx) {
		if (grid[idx] != 0)
			return false;
		Pair[] nearest = getNearestSix(idx);
		int cfilled = 0;
		for (int i = 0; i < nearest.length; i++) {
			int nidx = nearest[i].idx;
			if (grid[nidx] != 0) {
				cfilled++;
			}
		}
		if (cfilled > 1 || (cfilled == 1 && noFilled == 1))
			return true;
		else
			return false;

	}

	public Pair[] getNearestSix(int idx) {
		return neighbors[idx];
	}

	boolean isWinning(int idx) {
		return trapped(idx) || (countConsecutive(idx) >= 5);
	}

	public int countConsecutive(int idx) {
		int color = grid[idx];
		int x = xs[idx];
		int y = ys[idx];

		Pair[] neighbors = getNearestSix(idx);
		int line = 0;
		for (Pair p : neighbors) {
			int nx = xs[p.idx];
			int ny = ys[p.idx];
			if (color == grid[p.idx]) {
				lineVisited = new boolean[CELLS];
				int dir = (ny - y) / (nx - x);
				lineVisited[idx] = true;
				int line1 = checkLine(p.idx, dir, nx, ny, 2);
				int line2 = checkLine(idx, dir, x, y, 0);
				line = Math.max(line, line1 + line2);
			}
		}
		return line;
	}

	private int checkLine(int idx, int m, int x, int y, int depth) {
		lineVisited[idx] = true;
		if (depth == 5)
			return depth;
		int color = grid[idx];
		Pair[] neighbors = getNearestSix(idx);
		for (Pair p : neighbors) {
			int nx = xs[p.idx];
			int ny = ys[p.idx];
			int slope = (ny - y) / (nx - x);
			if (!lineVisited[p.idx] && color == grid[p.idx] && slope == m) {
				return checkLine(p.idx, slope, nx, ny, depth + 1);
			}
		}
		return depth;
	}

	private boolean trapped(int idx) {
		int searchfor;
		if (turn)
			searchfor = 2;
		else
			searchfor = 1;
		Pair[] neighbors = getNearestSix(idx);
		boolean allfree = true;
		for (int i = 0; i < neighbors.length; i++) {
			visited = new boolean[CELLS]; // this was a huge bug, I was initializing the array before the loop so
											// sometimes it doesn't find a way out to the edge
			int nidx = neighbors[i].idx;
			if (grid[nidx] == searchfor || grid[nidx] == 0) {
				opponentCount = 0;
				if (grid[nidx] == searchfor)
					opponentCount++;
				boolean componentFree = freeDfs(nidx, searchfor);
				if (opponentCount > 0)
					allfree &= componentFree;
			}
		}

		return !allfree;
	}

	private boolean freeDfs(int i, int searchfor) {
		visited[i] = true;
		Pair[] neighbors = getNearestSix(i);
		if (neighbors.length < 6)
			return true;
		boolean currentFree = false;
		for (Pair p : neighbors) {
			if (!visited[p.idx] && (grid[p.idx] == 0 || grid[p.idx] == searchfor)) {
				if (grid[p.idx] == searchfor)
					opponentCount++;
				currentFree |= freeDfs(p.idx, searchfor);
			}
		}
		return currentFree;
	}

	public int chainValueBFS(int move) {
		visited = new boolean[CELLS];
		visited[move] = true;
		int color = grid[move];
		Queue<Pair> q = new LinkedList<Pair>();
		Pair top = new Pair(0, move);
		q.add(top);

		// int bestBranching = 0;
		ArrayList<Pair>  tips = new ArrayList<>();
		while (!q.isEmpty()) {
			top = q.poll();
			int idx = top.idx;
			Pair[] neighbors = getNearestSix(idx);
			// int branching = 0;
			for (Pair p : neighbors) {
				if (!visited[p.idx] && grid[p.idx] == color) {
					visited[p.idx] = true;
					Pair p1 = new Pair(top.dist + 1, p.idx);
					q.add(p1);
					tips.add(p1);
				}
			}
		}
		
		double dist = dist(xs[move], ys[move], xs[top.idx], ys[top.idx]);
		int chainValue = (int) (top.dist * 100 / (dist == 0 ? 1 : dist)); // top.dist is the maximum depth reachable
		// System.out.println("knot value: " + knotValue);
		return chainValue;
	}

	public int enclosementValue(int move) {
		int searchfor;
		if (turn)
			searchfor = 2;
		else
			searchfor = 1;
		Pair[] neighbors = getNearestSix(move);
		int best = 0;
		for (int i = 0; i < neighbors.length; i++) {
			visited = new boolean[CELLS];
			int nidx = neighbors[i].idx;
			if (!visited[nidx] && (grid[nidx] == searchfor || grid[nidx] == 0))
				best = Math.max(best, trappedBFS(nidx, searchfor));
		}

		return best;
	}
	

	private int trappedBFS(int i, int searchfor) {
		visited[i] = true;
		Queue<Pair> q = new LinkedList<Pair>();
		q.add(new Pair(0, i));
		// int bestBranching = 0;
		int count = 0;
		while (!q.isEmpty() && q.peek().dist < 3) {
			Pair top = q.poll();
			int idx = top.idx;
			Pair[] neighbors = getNearestSix(idx);
			// int branching = 0;
			for (Pair p : neighbors) {
				if (!visited[p.idx]) {
					visited[p.idx] = true;
					if (grid[p.idx] != 0 && grid[p.idx] != searchfor) {
						count++;
					}
					q.add(new Pair(top.dist + 1, p.idx));
				}
			}
		}
		return count;
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
	
	double dist(int x, int y, int x2, int y2) {
		return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
	}

	static class Pair implements Comparable<Pair> {
		int dist;
		int idx;

		public Pair(int d, int i) {
			dist = d;
			idx = i;
		}

		@Override
		public int compareTo(Pair o) {
			return this.dist - o.dist;
		}

	}
	
}
