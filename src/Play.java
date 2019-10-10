
public class Play implements Comparable<Play>{
	int value;
	int pos;

	public Play(int value, int pos) {
		this.value = value;
		this.pos = pos;
	}

	@Override
	public int compareTo(Play o) {
		return o.value - this.value;
	}
	
	public String toString() {
		return Game.toString(this.pos) + " " + this.value;
	}
}
