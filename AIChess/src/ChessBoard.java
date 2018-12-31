import java.util.Map;

public class ChessBoard {
	// Chinese board's size.
	private final int BOARD_WIDTH = 9;
	private final int BOARD_HEIGHT = 10;
	private Piece[][] board = new Piece[BOARD_HEIGHT][BOARD_WIDTH];
	// 记录场上剩余的棋子
	public Map<String, Piece> pieces;
	
	// Players.
	public char player = 'r';
	
	// Judge whether the piece is in the board.
	public boolean isInside(int[] pos) {
		return isInside(pos[0], pos[1]);
	}
	
	public boolean isInside(int x, int y) {
		return !(x < 0 || x >= BOARD_HEIGHT
				|| y < 0 || y >= BOARD_WIDTH);
	}
	
	// Judge whether is empty or not.
	public boolean isEmpty(int[] pos) {
		return isEmpty(pos[0], pos[1]);
	}
	
	public boolean isEmpty(int x, int y) {
		return isInside(x, y) && board[x][y] == null;
	}
	
	// Update piece.
	public boolean update(Piece piece) {
		int[] pos = piece.pos;
		board[pos[0]][pos[1]] = piece;
		return true;
	}
	
	public Piece updatePiece(String key, int[] newPos) {
		Piece orig = pieces.get(key);
		Piece isNewPos = getPiece(newPos);
		
		// 如果棋局中该位置的棋子不为空，则将棋移走
		if (isNewPos != null) {
			pieces.remove(isNewPos.Info);
		}
		
		// 更新位置
		int[] origPos = orig.pos;
		board[origPos[0]][origPos[1]] = null;
		board[newPos[0]][newPos[1]] = orig;
		orig.pos = newPos;
		player = (player == 'r') ? 'b' : 'r';
		return isNewPos;
	}
	
	// Some methods for getting the info of the board.
	public boolean backPiece(String key) {
		int[] origPos = pieces.get(key).pos;
		board[origPos[0]][origPos[1]] = pieces.get(key);
		return true;
	}
	
	public Piece getPiece(int[] pos) {
		return getPiece(pos[0], pos[1]);
	}
	
	public Piece getPiece(int x, int y) {
		return board[x][y];
	}
}
