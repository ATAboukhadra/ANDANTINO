import java.util.ArrayList;
import java.util.Arrays;

public class IDMO {
	static Play[] oldMoves;


	public static Play idmo(State s, int maxDepth, int depth, int alpha, int beta, boolean Max, int color) {
		Search.allStates++;
		boolean winning = s.isWinning();
		if (winning || depth == 0) {
			if (winning)
				Search.winningStates++;
			int value = Search.evaluate(s, winning, color, depth);
			return new Play(value, s.idx);
		}

		ArrayList<State> successors = s.getSuccessors();

		if (depth == maxDepth) // root node
			successors = sort(successors);

		int score;
		Play bestPlay = null;
		if (Max) {
			score = Integer.MIN_VALUE;
			int i = 0;
			for (State succ : successors) {
				Play play = idmo(succ, maxDepth, depth - 1, alpha, beta, !Max, color);

				if (depth == maxDepth) { // root node
					oldMoves[i] = new Play(play.value, succ.idx);
					i++;
				}
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
			for (State succ : successors) {
				Play play = idmo(succ, maxDepth, depth - 1, alpha, beta, !Max, color);
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

	private static ArrayList<State> sort(ArrayList<State> successors) {
		if (oldMoves[0] == null) // 1st iteration
			return successors;
		else {
			ArrayList<State> sortedSuccessors = new ArrayList<>();
			Arrays.sort(oldMoves);
			for (int i = 0; i < oldMoves.length; i++) {
				int idx = oldMoves[i].pos;
				for (State state : successors) {
					if (state.idx == idx) {
						sortedSuccessors.add(state);
						break;
					}
				}
			}
			return sortedSuccessors;
		}
	}

}
