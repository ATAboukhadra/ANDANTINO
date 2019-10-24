
public class Pair implements Comparable<Pair> {
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

	public String toString() {
		return Game.toString(idx);
	}
}
