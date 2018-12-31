import java.util.ArrayList;
import java.util.Map;

// Node used to record the path.
class AlphaBetaNode {
	public String piece_key;
	public int[] from;
	public int[] to;
	public int value;
	
	public AlphaBetaNode(String piece_key, int[] from, int[] to) {
		this.piece_key = piece_key;
		this.from = from;
		this.to= to;
	}
}

// Alpha-Beta Algorithm
// Finally return a AlphaBetaNode, record which piece and position.
public class AlphaBetaAlgorithm {
	private int depth = 2;
	private ChessBoard Board;
	private GameController controller = new GameController();
	int[] BasicValue = { 0, 0, 250, 250, 300, 500, 300 };
	
	// 0
	int[][] JiangPosition = new int[][] {
		{0, 0, 0, 1, 5, 1, 0, 0, 0},
		{0, 0, 0, -8, -8, -8, 0, 0, 0},
		{0, 0, 0, -9, -9, -9, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0}
	};
	
	// 1
	int[][] ShiPosition = new int[][] {
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 3, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0}
	};
	
	// 2
	int[][] XiangPosition = new int[][] {
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{-2, 0, 0, 0, 3, 0, 0, 0, -2},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0}
	};
	
	// 3
	int[][] MaPosition = new int[][] {
		{0, -3, 2, 0, 2, 0, 2, -3, 0},
		{-3, 2, 4, 5, -10, 5, 4, 2, -3},
		{5, 4, 6, 7, 4, 7, 6, 4, 5},
		{4, 6, 10, 7, 10, 7, 10, 6, 4},
		{2, 10, 13, 14, 15, 14, 13, 10, 2},
		{2, 10, 13, 14, 15, 14, 13, 10, 2},
		{2, 12, 11, 15, 16, 15, 11, 12, 2},
		{5, 20, 12, 19, 12, 19, 12, 20, 5},
		{4, 10, 11, 15, 11, 15, 11, 10, 4},
		{2, 8, 15, 9, 6, 9, 15, 8, 2},
		{2, 2, 2, 8, 2, 8, 2, 2, 2}
	};
	
	// 4
	int[][] JuPosition = new int[][] {
		{-6, 6, 4, 12, 0, 12, 4, 6, -6},
		{5, 8, 6, 12, 0, 12, 6, 8, 5},
		{-2, 8, 4, 12, 12, 12, 4, 8, -2},
		{4, 9, 4, 12, 14, 12, 4, 9, 4},
		{8, 12, 12, 14, 15, 14, 12, 12, 8},
		{8, 11, 11, 14, 15, 14, 11, 11, 8},
		{6, 13, 13, 16, 16, 16, 13, 13, 6},
		{6, 8, 7, 14, 16, 14, 7, 8, 6},
		{6, 12, 9, 16, 33, 16, 9, 12, 6},
		{6, 8, 7, 13, 14, 13, 7, 8, 6}
	};
	
	// 5
	int[][] PaoPosition = new int[][] {
		{0, 0, 1, 3, 3, 3, 1, 0, 0},
		{0, 1, 2, 2, 2, 2, 2, 1, 0},
		{1, 0, 4, 3, 5, 3, 4, 0, 1},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{-2, 0, -2, 0, 6, 0, -2, 0, -2},
		{3, 0, 4, 0, 7, 0, 4, 0, 3},
		{10, 18, 22, 35, 40, 35, 22, 18, 10},
		{20, 27, 30, 40, 42, 40, 30, 27, 20},
		{20, 30, 45, 55, 55, 55, 45, 30, 20},
		{20, 30, 50, 65, 70, 65, 50, 30, 20},
		{0, 0, 0, 2, 4, 2, 0, 0, 0}
	};
	
	// 6
	int[][] BingPosition = new int[][] {
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0},
		{-2, 0, -2, 0, 6, 0, -2, 0, -2},
		{3, 0, 4, 0, 7, 0, 4, 0, 3},
		{10, 18, 22, 35, 40, 35, 22, 18, 10},
		{20, 27, 30, 40, 42, 40, 30, 27, 20},
		{20, 30, 50, 65, 70, 65, 50, 30, 20},
		{0, 0, 0, 2, 4, 2, 0, 0, 0}
	};
	
	public AlphaBetaNode search(ChessBoard board) {
		this.Board = board;
		ArrayList<AlphaBetaNode> allNextStep = new ArrayList<AlphaBetaNode>();
		for (Map.Entry<String, Piece> entry : Board.pieces.entrySet()) {
			Piece piece = entry.getValue();
			if (piece.color == 'b') {
				for (int[] next : Rule.getNextMove(piece.Info, piece.pos, Board)) {
					AlphaBetaNode newNode = new AlphaBetaNode(piece.Info, piece.pos, next);
					allNextStep.add(newNode);
				}				
			}
		}
		
		AlphaBetaNode best = null;
		for (AlphaBetaNode n : allNextStep) {
			Piece p = Board.getPiece(n.to);
			if (p != null && p.character == 'b') {
				return n;
			}
		}
		
		long start = System.currentTimeMillis();
		for (AlphaBetaNode n : allNextStep) {
			Piece eaten = Board.updatePiece(n.piece_key, n.to);
			if (eaten != null) n.value += 100;
			n.value = alpha_beta_search(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			if (best == null || n.value >= best.value) {
				best = n;
			}
			Board.updatePiece(n.piece_key, n.from);
			if (eaten != null) {
				board.pieces.put(eaten.Info, eaten);
				board.backPiece(eaten.Info);
			}
		}
		long finish = System.currentTimeMillis();
		
		System.out.println("Calculate Time: " + (finish-start) + "ms");
		System.out.println("From: (" + best.from[0] + ", " + best.from[1] + ") to (" + best.to[0] + ", " + best.to[1] + ")");
		return best;
	}
	
	private int[] getOppositePos(int[] pos) {
		int[] result = new int[] {9-pos[0], pos[1]};
		return result;
	}
	
	private int estimate_value(int piece) {
		return BasicValue[piece];
	}
	
	private int estimate_position(int piece, int[] pos) {
		switch (piece) {
			case 0 :
				return JiangPosition[pos[0]][pos[1]];
			case 1 :
				return ShiPosition[pos[0]][pos[1]];
			case 2 :
				return XiangPosition[pos[0]][pos[1]];
			case 3 :
				return MaPosition[pos[0]][pos[1]];
			case 4 :
				return JuPosition[pos[0]][pos[1]];
			case 5 :
				return PaoPosition[pos[0]][pos[1]];
			case 6 :
				return BingPosition[pos[0]][pos[1]];
			default :
				return -1;
		}
	}
	
	private int estimate_myself(Piece piece) {
		// System.out.println(piece.Info);
		if (piece.Info == "bb0" || piece.Info == "rb0") return 0;
		int totalValue = 0;
		ArrayList<int[]> next = Rule.getNextMove(piece.Info, piece.pos, Board);
		for (int[] n : next) {
			Piece p = Board.getPiece(n);
			if (p != null && Board.getPiece(n).character == 'b') {
				totalValue += 9999;
				break;
			}
			if (p != null && Board.getPiece(n).character == 'j') {
				totalValue += 500;
				break;
			}
			if (p != null && Board.getPiece(n).character == 'm' &&  Board.getPiece(n).character == 'p') {
				totalValue += 100;
			}
			if (p != null && Board.getPiece(n).character == 'z') {
				totalValue -= 20;
			}
		}
		return totalValue;
	}
	
	public int estimate(ChessBoard Board) {
		int[][] totalValue = new int[2][3];
		for (Map.Entry<String, Piece> pieceEntry : Board.pieces.entrySet()) {
			Piece piece = pieceEntry.getValue();
			switch (piece.character) {
				case 'b':
					if (piece.color == 'b') {
						totalValue[0][0] += estimate_value(0);
						totalValue[0][1] += estimate_position(0, piece.pos);
					} else {
						totalValue[1][0] += estimate_value(0);
						totalValue[1][1] += estimate_position(0, getOppositePos(piece.pos));
					}
					break;
				case 's':
					if (piece.color == 'b') {
						totalValue[0][0] += estimate_value(1);
						totalValue[0][1] += estimate_position(1, piece.pos);
					} else {
						totalValue[1][0] += estimate_value(1);
						totalValue[1][1] += estimate_position(1, getOppositePos(piece.pos));
					}
					break;
				case 'x':
					if (piece.color == 'b') {
						totalValue[0][0] += estimate_value(2);
						totalValue[0][1] += estimate_position(2, piece.pos);
					} else {
						totalValue[1][0] += estimate_value(2);
						totalValue[1][1] += estimate_position(2, getOppositePos(piece.pos));
					}
					break;
				case 'm':
					if (piece.color == 'b') {
						totalValue[0][0] += estimate_value(3);
						totalValue[0][1] += estimate_position(3, piece.pos);
					} else {
						totalValue[1][0] += estimate_value(3);
						totalValue[1][1] += estimate_position(3, getOppositePos(piece.pos));
					}
					break;
				case 'j':
					if (piece.color == 'b') {
						totalValue[0][0] += estimate_value(4);
						totalValue[0][1] += estimate_position(4, piece.pos);
					} else {
						totalValue[1][0] += estimate_value(4);
						totalValue[1][1] += estimate_position(4, getOppositePos(piece.pos));
					}
					break;
				case 'p':
					if (piece.color == 'b') {
						totalValue[0][0] += estimate_value(5);
						totalValue[0][1] += estimate_position(5, piece.pos);
					} else {
						totalValue[1][0] += estimate_value(5);
						totalValue[1][1] += estimate_position(5, getOppositePos(piece.pos));
					}
					break;
				case 'z':
					if (piece.color == 'b') {
						totalValue[0][0] += estimate_value(6);
						totalValue[0][1] += estimate_position(6, piece.pos);
					} else {
						totalValue[1][0] += estimate_value(6);
						totalValue[1][1] += estimate_position(6, getOppositePos(piece.pos));
					}
					break;
			}
			totalValue[0][2] += estimate_myself(piece);
			totalValue[1][2] += estimate_myself(piece);
		}
		int red = totalValue[1][0] + totalValue[1][1] + totalValue[1][2];
		int black = totalValue[0][0] + totalValue[0][1] + totalValue[0][2];
		int result_value = black - red;
		return result_value;
	}
	
	// alpha-beta algorithm.
	private int alpha_beta_search(int depth, int alpha, int beta, boolean isMax) {
		// Recursion end: if the depth is 0 or Gameover.
		if (depth == 0 || controller.hasWin(Board) != 'x') {
			return estimate(Board);
		}
		
		// Generate all the situation the current position will go.
		// 只有在极大值层的时候才会生成
		ArrayList<AlphaBetaNode> allNextStep = new ArrayList<AlphaBetaNode>();
		for (Map.Entry<String, Piece> entry : Board.pieces.entrySet()) {
			Piece piece = entry.getValue();
			if ((piece.color == 'b' && isMax) || (piece.color == 'r' && !isMax)) {
				for (int[] next : Rule.getNextMove(piece.Info, piece.pos, Board)) {
					AlphaBetaNode newNode = new AlphaBetaNode(piece.Info, piece.pos, next);
					allNextStep.add(newNode);
				}
			}
		}
		
		for (AlphaBetaNode n : allNextStep) {
			Piece Eaten_piece = Board.updatePiece(n.piece_key, n.to);
			if (isMax) {
				alpha = Math.max(alpha, alpha_beta_search(depth-1, alpha, beta, false));
			} else {
				beta = Math.min(beta, alpha_beta_search(depth-1, alpha, beta, true));
			}
			Board.updatePiece(n.piece_key, n.from);
			if (Eaten_piece != null) {
				Board.pieces.put(Eaten_piece.Info, Eaten_piece);
				Board.backPiece(Eaten_piece.Info);
			}
			
			// 剪枝过程
			if (beta <= alpha) break;
		}	
		return isMax ? alpha : beta;
	}
}
