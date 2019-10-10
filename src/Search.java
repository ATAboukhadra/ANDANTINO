public class Search {

	static final int BLACK = 1;
	static final int WHITE = 2;

	public static int winningstates = 0;
	public static int allstates = 0;

	public static Play miniMax(State s, int depth, boolean Max, int color) {
		allstates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				winningstates++;
			int value = evaluate(s, winning, color);
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
		allstates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				winningstates++;
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
		allstates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				winningstates++;
			int value = evaluate(s, winning, color);
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

	public static Play IDMO(State s, int maxDepth, int color) { // Iterative Deepening with Move ordering
		return IDMO.idmo(s, maxDepth, color);
	}

	public static Play alphaBetaTT(State s, int depth, int alpha, int beta, boolean Max, int color) {
		int olda = alpha;
		TTEntry n = retrieve(s);
		if (n.depth >= depth) {
			if (n.typeOfValue == Type.EXACT) {
				return new Play(n.value, n.bestMove);
			} else if (n.typeOfValue == Type.LOWER) {
				alpha = Math.max(alpha, n.value);
			} else {
				beta = Math.min(beta, n.value);
			}
			if (alpha >= beta)
				return new Play(n.value, n.bestMove);
		}

		allstates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				winningstates++;
			return new Play(evaluate(s, winning, color), s.idx);
		}
		int score = Integer.MIN_VALUE;
		Play bestPlay = null;

		for (State succ : s.getSuccessors()) {
			Play play = alphaBetaTT(succ, depth - 1, -1 * beta, -1 * alpha, !Max, color);
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
		Type flag;
		if (score <= olda)
			flag = Type.UPPER;
		else if (score >= beta)
			flag = Type.LOWER;
		else
			flag = Type.EXACT;

		store(s, bestPlay.pos, score, flag, depth);

		return bestPlay;
	}

	static TTEntry retrieve(State s) {
		return null;
	}

	static void store(State s, int play, int score, Type flag, int depth) {

	}

	public static int evaluate(State s, boolean winning, int colorFlag) { // Max is black and MIN is white
		int lastColor = s.grid[s.idx];
		boolean iWin = lastColor == colorFlag;
		// boolean iWin = Max;
		if (winning) {
			return iWin ? 500 : -500;
		} else {
			int value = 0;
			// am I going to make a full row?
			value += s.countConsecutive(s.idx) * (iWin ? 10 : -10);
			// if I put my piece next to my other pieces, I can't lose from trap, I may lose
			// from enemies consecutives or win with my consecutives
			// I care abput putting my pieces together (defensive mechanism), I don't care a
			// lot about enemies pieces
			value += s.countComponent(s.idx) * (iWin ? 4 : -1);
			// let's count how many enemy cells can't reach the edge
			// value += s.enclosementValue(s.idx);
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
