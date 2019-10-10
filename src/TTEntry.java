
public class TTEntry {
	int value;
	Type typeOfValue;
	int bestMove;
	int depth;
	long key; // for verification
	public TTEntry(int value, Type typeOfValue, int bestMove, int depth, long key) {
		this.value = value;
		this.typeOfValue = typeOfValue;
		this.bestMove = bestMove;
		this.depth = depth;
		this.key = key;
	}
}
