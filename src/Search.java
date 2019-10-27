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

	public static Play negaMax(State s, int depth, int alpha, int beta, boolean Max, int color) {
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
			Play play = negaMax(succ, depth - 1, -1 * beta, -1 * alpha, !Max, color);
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
				if (beta <= alpha)
					break;

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
				if (beta <= alpha)
					break;

			}
		}
		return bestPlay;
	}

	public static Play alphaBetaTT(State s, int depth, int alpha, int beta, boolean Max, int color) {
		TranspositionTable.TT = new HashMap<>();
		TranspositionTable.errors = 0;
		originalDepth = depth;
		Play p = TranspositionTable.alphaBetaTT(s, depth, alpha, beta, Max, color);
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
			allStates = 0;

			p = TranspositionTable.alphaBetaTT(s, i, -inf, inf, true, color);
			currentTime = System.currentTimeMillis();
		}
		String time = String.format("%.2f", (currentTime - startTime) / 1000);
		System.out.println("Reached Depth " + (i - 1) + " in " + time + " Seconds");
		System.out.println("# of hits = " + TranspositionTable.hits + " ---- # of errors = " + TranspositionTable.errors
				+ " ---- Table size = " + TranspositionTable.TT.size());
		return p;
	}

	public static Play alphaBetaTTKillerMoves(State s, int depth, int alpha, int beta, boolean Max, int color) {
		TranspositionTable.TT = new HashMap<>();
		TranspositionTable.errors = 0;
		originalDepth = depth;
		TranspositionTable.kmDepth = depth;
		TranspositionTable.killerMoves = new int[depth][2];
		Play p = TranspositionTable.alphaBetaTTKillerMoves(s, depth, alpha, beta, Max, color);
		System.out.println(TranspositionTable.errors);
		return p;
	}

	public static Play alphaBetaIDTTKillerMoves(State s, int maxDepth, int color) {
		int inf = (int) 1e9;
		double startTime = System.currentTimeMillis();
		double timeLimit = 3000; // 3 seconds
		double endTime = startTime + timeLimit;
		double currentTime = startTime;
		Play p = null;
		TranspositionTable.TT = new HashMap<>();
		TranspositionTable.hits = 0;
		TranspositionTable.errors = 0;
		int moves = s.getSuccessors().size();
//		System.out.println("# of valid moves = " + moves);
		// this is done to control the depth at very high branching factors to avoid
		// crashing
		if (moves > 22)
			maxDepth = 6;
		if (moves > 24)
			maxDepth = 5;

		int i;
		for (i = 1; i <= maxDepth && currentTime < endTime; i++) {
			originalDepth = i;
			allStates = 0;
			TranspositionTable.kmDepth = i;
			TranspositionTable.killerMoves = new int[i][2];
			TranspositionTable.killers = 0;
			p = TranspositionTable.alphaBetaTTKillerMoves(s, i, -inf, inf, true, color);
			currentTime = System.currentTimeMillis();
			String time = String.format("%.2f", (currentTime - startTime) / 1000);
			if (i > 4)
				System.out.println("Depth reached so far = " + (i) + " in " + time + " Seconds "
						+ "# of effective killer moves = " + TranspositionTable.killers);

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
//		System.out.println("# of valid moves = " + size);
		
		IDMO.oldMoves = new Play[size];
		int i;
		for (i = 1; i <= maxDepth && currentTime < endTime; i++) {
			originalDepth = i;
			Search.allStates = 0;
			p = IDMO.idmo(s, i, i, -inf, inf, true, color);
			currentTime = System.currentTimeMillis();
			String time = String.format("%.2f", (currentTime - startTime) / 1000);
			if (i > 4)
				System.out.println("Depth reached so far = " + (i) + " in " + time + " Seconds "
						+ "# of effective killer moves = " + TranspositionTable.killers);

		}
		String time = String.format("%.2f", (currentTime - startTime) / 1000);
		System.out.println("Reached Depth " + (i - 1) + " in " + time + " Seconds");
		return p;
	}

	public static int evaluate(State s, boolean winning, int colorFlag, int depth) { // Max is black and MIN is white
		return Evaluator.evaluate(s, winning, colorFlag, depth, originalDepth);
	}

	public static int evaluateNegaMax(State s, boolean winning) {
		if (winning) {
			return -500;
		} else {
			return s.countConsecutive(s.idx) * -10;
		}
	}

}
