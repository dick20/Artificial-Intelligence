
public class AIChineseChess {
	private ChessBoard Board;
	private GameController controller;
	private GameView view;
	
	public static void main(String[] args) throws InterruptedException {
		AIChineseChess game = new AIChineseChess();
		game.init();
		game.run();
	}
	
    public void init() {
        controller = new GameController();
        Board = controller.playChess();

        view = new GameView(controller);
        view.init(Board);
    }
    
    public void run() throws InterruptedException {
        while (controller.hasWin(Board) == 'x') {
            view.showPlayer('r');
            /* User in. */
            while (Board.player == 'r')
                Thread.sleep(1000);

            if (controller.hasWin(Board) != 'x')
                view.showWinner('r');
            view.showPlayer('b');
            /* AI in. */
            controller.responseMoveChess(Board, view);
        }
        view.showWinner('b');
    }
}
