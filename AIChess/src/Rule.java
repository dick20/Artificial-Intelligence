import java.util.ArrayList;

// 定义象棋行走的规则
public class Rule {
	private static int[] pos;
	private static ChessBoard Board;
	private static char player;
	
	public static ArrayList<int[]> getNextMove(String piece, int[] pos, ChessBoard board) {
		Rule.pos = pos;
		Rule.Board = board;
		Rule.player = piece.charAt(0);
		switch (piece.charAt(1)) {
			case 'j':
				return Ju();
			case 'm':
				return Ma();
			case 'p':
				return Pao();
			case 'x':
				return Xiang();
			case 's':
				return Shi();
			case 'b':
				return Jiang();
			case 'z':
				return Bing();
			default:
				return null;
		}
	}
	
	// 马的行棋规则及可能的行走空间
	private static ArrayList<int[]> Ma() {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		int[][] target = new int[][]{{1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}};
		int[][] obstacle = new int[][]{{0, -1}, {1, 0}, {1, 0}, {0, 1}, {0, 1}, {-1, 0}, {-1, 0}, {0, -1}};
		for (int i = 0; i < target.length; i++) {
			int[] e = new int[]{pos[0] + target[i][0], pos[1] + target[i][1]};
			int[] f = new int[]{pos[0] + obstacle[i][0], pos[1] + obstacle[i][1]};
			if (!Board.isInside(e)) continue;
			if (Board.isEmpty(f)) {
				if (Board.isEmpty(e)) moves.add(e);
				else if (Board.getPiece(e).color != player) moves.add(e);
			}
		}
		return moves;
	}
	
	// 车的行棋规则及可能的行走空间
    private static ArrayList<int[]> Ju() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        int[] yOffsets = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        int[] xOffsets = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int offset : yOffsets) {
            int[] rMove = new int[]{pos[0], pos[1] + offset};
            if (Board.isEmpty(rMove)) moves.add(rMove);
            else if (Board.isInside(rMove) && Board.getPiece(rMove).color != player) {
                moves.add(rMove);
                break;
            } else break;
        }
        for (int offset : yOffsets) {
            int[] lMove = new int[]{pos[0], pos[1] - offset};
            if (Board.isEmpty(lMove)) moves.add(lMove);
            else if (Board.isInside(lMove) && Board.getPiece(lMove).color != player) {
                moves.add(lMove);
                break;
            } else break;
        }
        for (int offset : xOffsets) {
            int[] uMove = new int[]{pos[0] - offset, pos[1]};
            if (Board.isEmpty(uMove)) moves.add(uMove);
            else if (Board.isInside(uMove) && Board.getPiece(uMove).color != player) {
                moves.add(uMove);
                break;
            } else break;
        }
        for (int offset : xOffsets) {
            int[] dMove = new int[]{pos[0] + offset, pos[1]};
            if (Board.isEmpty(dMove)) moves.add(dMove);
            else if (Board.isInside(dMove) && Board.getPiece(dMove).color != player) {
                moves.add(dMove);
                break;
            } else break;
        }
        return moves;
    }	
    
    // 炮的行走规则及可能的行棋空间
    private static ArrayList<int[]> Pao() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        int[] yOffsets = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        int[] xOffsets = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        boolean rr = false, ll = false, uu = false, dd = false;
        for (int offset : yOffsets) {
            int[] rMove = new int[]{pos[0], pos[1] + offset};
            if (!Board.isInside(rMove)) break;
            boolean e = Board.isEmpty(rMove);
            if (!rr) {
                if (e) moves.add(rMove);
                else rr = true;
            } else if (!e) {
                if (Board.getPiece(rMove).color != player) moves.add(rMove);
                break;
            }
        }
        for (int offset : yOffsets) {
            int[] lMove = new int[]{pos[0], pos[1] - offset};
            if (!Board.isInside(lMove)) break;
            boolean e = Board.isEmpty(lMove);
            if (!ll) {
                if (e) moves.add(lMove);
                else ll = true;
            } else if (!e) {
                if (Board.getPiece(lMove).color != player) {
                    moves.add(lMove);
                }
                break;
            }
        }
        for (int offset : xOffsets) {
            int[] uMove = new int[]{pos[0] - offset, pos[1]};
            if (!Board.isInside(uMove)) break;
            boolean e = Board.isEmpty(uMove);
            if (!uu) {
                if (e) moves.add(uMove);
                else uu = true;
            } else if (!e) {
                if (Board.getPiece(uMove).color != player) moves.add(uMove);
                break;
            }
        }
        for (int offset : xOffsets) {
            int[] dMove = new int[]{pos[0] + offset, pos[1]};
            if (!Board.isInside(dMove)) break;
            boolean e = Board.isEmpty(dMove);
            if (!dd) {
                if (e) moves.add(dMove);
                else dd = true;
            } else if (!e) {
                if (Board.getPiece(dMove).color != player) moves.add(dMove);
                break;
            }
        }
        return moves;
    }
    
    // 象的行棋规则及可能的行走步数
    private static ArrayList<int[]> Xiang() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        int[][] target = new int[][]{{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        int[][] obstacle = new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
        for (int i = 0; i < target.length; i++) {
            int[] e = new int[]{pos[0] + target[i][0], pos[1] + target[i][1]};
            int[] f = new int[]{pos[0] + obstacle[i][0], pos[1] + obstacle[i][1]};
            if (!Board.isInside(e) || (e[0] > 4 && player == 'b') || (e[0] < 5 && player == 'r')) continue;
            if (Board.isEmpty(f)) {
                if (Board.isEmpty(e)) moves.add(e);
                else if (Board.getPiece(e).color != player) moves.add(e);
            }
        }
        return moves;
    }
    
    // 士的行棋规则及可能的行走步数
    private static ArrayList<int[]> Shi() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        int[][] target = new int[][]{{-1, -1}, {1, 1}, {-1, 1}, {1, -1}};
        for (int[] aTarget : target) {
            int[] e = new int[]{pos[0] + aTarget[0], pos[1] + aTarget[1]};
            if (!Board.isInside(e) || ((e[0] > 2 || e[1] < 3 || e[1] > 5) && player == 'b') || ((e[0] < 7 || e[1] < 3 || e[1] > 5) && player == 'r'))
                continue;
            if (Board.isEmpty(e)) moves.add(e);
            else if (Board.getPiece(e).color != player) moves.add(e);
        }
        return moves;
    }
    
    // 将军的行棋规则及可能的行走步数
    private static ArrayList<int[]> Jiang() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        /* 3*3 block */
        int[][] target = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] aTarget : target) {
            int[] e = new int[]{pos[0] + aTarget[0], pos[1] + aTarget[1]};
            if (!Board.isInside(e) || ((e[0] > 2 || e[1] < 3 || e[1] > 5) && player == 'b') || ((e[0] < 7 || e[1] < 3 || e[1] > 5) && player == 'r'))
                continue;
            if (Board.isEmpty(e)) moves.add(e);
            else if (Board.getPiece(e).color != player) moves.add(e);
        }
        /* opposite 'b' */
        boolean flag = true;
        int[] oppoBoss = (player == 'r') ? Board.pieces.get("bb0").pos : Board.pieces.get("rb0").pos;
        if (oppoBoss[1] == pos[1]) {
            for (int i = Math.min(oppoBoss[0], pos[0]) + 1; i < Math.max(oppoBoss[0], pos[0]); i++) {
                if (Board.getPiece(i, pos[1]) != null) {
                    flag = false;
                    break;
                }
            }
            if (flag) moves.add(oppoBoss);
        }
        return moves;
    }    
    
    // 兵的行棋规则及可能的行走步数
    private static ArrayList<int[]> Bing() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        int[][] targetU = new int[][]{{0, 1}, {0, -1}, {-1, 0}};
        int[][] targetD = new int[][]{{0, 1}, {0, -1}, {1, 0}};
        if (player == 'r') {
            if (pos[0] > 4) {
                int[] e = new int[]{pos[0] - 1, pos[1]};
                if (Board.isEmpty(e)) moves.add(e);
                else if (Board.getPiece(e).color != player) moves.add(e);
            } else {
                for (int[] aTarget : targetU) {
                    int[] e = new int[]{pos[0] + aTarget[0], pos[1] + aTarget[1]};
                    if (!Board.isInside(e)) continue;
                    if (Board.isEmpty(e)) moves.add(e);
                    else if (Board.getPiece(e).color != player) moves.add(e);
                }
            }
        }
        if (player == 'b') {
            if (pos[0] < 5) {
                int[] e = new int[]{pos[0] + 1, pos[1]};
                if (Board.isEmpty(e)) moves.add(e);
                else if (Board.getPiece(e).color != player) moves.add(e);
            } else {
                for (int[] aTarget : targetD) {
                    int[] e = new int[]{pos[0] + aTarget[0], pos[1] + aTarget[1]};
                    if (!Board.isInside(e)) continue;
                    if (Board.isEmpty(e)) moves.add(e);
                    else if (Board.getPiece(e).color != player) moves.add(e);
                }
            }
        }

        return moves;
    }    
}
