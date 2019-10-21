import java.util.HashMap;

public class Search {

	static final int BLACK = 1;
	static final int WHITE = 2;

	public static int winningStates = 0;
	public static int allStates = 0;

	public static int inf = 1000;
	public static int originalDepth = 0;

	public static Play miniMax(State s, int depth, boolean Max, int color) {
		allStates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				winningStates++;
			int value = evaluate(s, winning, color, depth);
			return new Play(value, s.idx);
		}
		int score;
		Play bestPlay = null;
		if (Max) {
			score = Integer.MIN_VALUE;
			for (State succ : s.getSuccessors()) {
				Play play = miniMax(succ, depth - 1, !Max, color);
				if (play.value >= score) {
					score = play.value;
					bestPlay = new Play(score, succ.idx);
				}
			}
		} else {
			score = Integer.MAX_VALUE;
			for (State succ : s.getSuccessors()) {
				Play play = miniMax(succ, depth - 1, !Max, color);
				if (play.value <= score) {
					score = play.value;
					bestPlay = new Play(score, succ.idx);
				}
			}
		}
		return bestPlay;
	}

	public static Play alphaBetaNegaMax(State s, int depth, int alpha, int beta, boolean Max, int color) {
		allStates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				winningStates++;
			return new Play(evaluateNegaMax(s, winning), s.idx);
		}
		int score = Integer.MIN_VALUE;
		Play bestPlay = null;

		for (State succ : s.getSuccessors()) {
			Play play = alphaBetaNegaMax(succ, depth - 1, -1 * beta, -1 * alpha, !Max, color);
			int value = play.value * -1;
			if (value > score) {
				score = value;
				bestPlay = new Play(score, succ.idx);
			}
			if (score > alpha)
				alpha = score;
			if (score >= beta)
				break;
		}

		return bestPlay;
	}

	public static Play alphaBeta(State s, int depth, int alpha, int beta, boolean Max, int color) {
		allStates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				winningStates++;
			int value = evaluate(s, winning, color, depth);
			return new Play(value, s.idx);
		}
		int score;
		Play bestPlay = null;
		if (Max) {
			score = Integer.MIN_VALUE;
			for (State succ : s.getSuccessors()) {
				Play play = alphaBeta(succ, depth - 1, alpha, beta, !Max, color);
				if (play.value > score) {
					score = play.value;
					bestPlay = new Play(score, succ.idx);
				}
				alpha = Math.max(alpha, score);
				if (beta <= alpha) {
					// System.out.println("pruned at depth " + depth);
					break;
				}
			}
		} else {
			score = Integer.MAX_VALUE;
			for (State succ : s.getSuccessors()) {
				Play play = alphaBeta(succ, depth - 1, alpha, beta, !Max, color);
				if (play.value < score) {
					score = play.value;
					bestPlay = new Play(score, succ.idx);
				}
				beta = Math.min(beta, score);
				if (beta <= alpha) {
					// System.out.println("pruned at depth " + depth);
					break;
				}
			}
		}
		return bestPlay;
	}

	public static Play alphaBetaTT(State s, int depth, int alpha, int beta, boolean Max, int color) {
		TranspositionTable.TT = new HashMap<>();
		TranspositionTable.errors = 0;
		originalDepth = depth;
		Play p = TranspositionTable.alphaBetaTT(s, depth, alpha, beta, Max, color);
		System.out.println(TranspositionTable.errors);
		return p;
	}

	public static Play alphaBetaIDTT(State s, int maxDepth, int color) {
		int inf = (int) 1e9;
		double startTime = System.currentTimeMillis();
		double timeLimit = 3000; // 3 seconds
		double endTime = startTime + timeLimit;
		double currentTime = startTime;
		Play p = null;
		TranspositionTable.TT = new HashMap<>();
		TranspositionTable.hits = 0;
		TranspositionTable.errors = 0;
		int i;
		for (i = 1; i <= maxDepth && currentTime < endTime; i++) {
			originalDepth = i;
			p = TranspositionTable.alphaBetaTT(s, i, -inf, inf, true, color);
			currentTime = System.currentTimeMillis();
		}
		String time = String.format("%.2f", (currentTime - startTime) / 1000);
		System.out.println("Reached Depth " + (i - 1) + " in " + time + " Seconds");
		System.out.println("# of hits = " + TranspositionTable.hits + " ---- # of errors = " + TranspositionTable.errors
				+ " ---- Table size = " + TranspositionTable.TT.size());
		return p;
	}

	public static Play idmo(State s, int maxDepth, int color) { // Iterative Deepening with Move ordering
		int inf = (int) 1e9;
		double startTime = System.currentTimeMillis();
		double timeLimit = 3000; // 3 seconds
		double endTime = startTime + timeLimit;
		double currentTime = startTime;
		Play p = null;
		int size = s.getSuccessors().size();
		IDMO.oldMoves = new Play[size];
		int i;
		for (i = 1; i <= maxDepth && currentTime < endTime; i++) {
			originalDepth = i;
			p = IDMO.idmo(s, i, i, -inf, inf, true, color);
			currentTime = System.currentTimeMillis();
		}
		String time = String.format("%.2f", (currentTime - startTime) / 1000);
		System.out.println("Reached Depth " + (i - 1) + " in " + time + " Seconds");
		return p;
	}

	public static int evaluate(State s, boolean winning, int colorFlag, int depth) { // Max is black and MIN is white
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
			value += s.countConsecutive(s.idx) * (iWin ? 9 : -9);
			// if I put my piece next to my other pieces, I can't lose from trap, I may lose
			// from enemies consecutives or win with my consecutives
			// I care about putting my pieces together (defensive mechanism), I don't care a
			// lot about enemies pieces
			value += s.countComponent(s.idx) * (iWin ? 3 : -1);
			// let's count how many pieces am I surrounding my enemy with
			value += s.enclosementValue(s.idx) * (iWin ? 5 : -5);
			// Knot Value, the problem is that is considers all the chain
			value += s.chainValueBFS(s.idx) * (iWin ? 6 : -6); // minimize distance
			return value;
		}
	}

	public static int evaluateNegaMax(State s, boolean winning) {
		if (winning) {
			return -500;
		} else {
			return s.countConsecutive(s.idx) * -10;
		}
	}

}
