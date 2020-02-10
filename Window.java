import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Window extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static int BOARD_DIMS = 8;
    private Board board;
    public static int WINDOW_WIDTH = 500;
    public static int WINDOW_HEIGHT = 500;

    public Window(int width, int height, String title) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 1, 0, 0));
        board = new Board(WINDOW_WIDTH, WINDOW_HEIGHT);
        add(board);
        setVisible(true);
    }

    public void end() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static String getColor(Color c){
        if (c == Color.red || c == Color.magenta){
            return "Red";
        } else {
            return "Blue";
        }
    }

    public void playAs(Color side, String select, boolean isRunning){
        try {
            if (select.contains("q") || select.contains("Q")) {
                isRunning = false;
                this.end();
            }else if (select.contains("end")){
                if (this.board.getHasMovedThisTurn()){
                    this.board.endTurn();
                }
            } else if (select.contains("undo")){
                this.board.undoLastMove();
            } else if (select.contains("p") || select.contains("P")) {
                this.board.printBoard();
            } else if (select.contains("c") || select.contains("C")) {
                String[] cmd = select.split(",");
                int x = Integer.parseInt(cmd[1]);
                int y = Integer.parseInt(cmd[2]);
                this.board.printPossibleMoves(x, y);
            } else if (select.contains(",")) {
                String[] xy = select.split(",");
                int startx = Integer.parseInt(xy[0]);
                int starty = Integer.parseInt(xy[1]);
                int endx = Integer.parseInt(xy[2]);
                int endy = Integer.parseInt(xy[3]);
                Piece piece = this.board.getBoard()[startx][starty];
                if (piece.getSide() == side){
                    this.board.move(piece, endx, endy);
                } else{
                    System.out.println("It is " + getColor(piece.getEnemySide()) + "'s turn!");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid Input");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Window window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "CHECKERS");
        boolean isRunning = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (isRunning) {
            System.out.print("Q,P,undo,(C,x,y),(sX,sY,dX,dY): ");
            String select = reader.readLine();
            window.playAs(window.board.currentTurn(), select, isRunning);
            isRunning = !window.board.gameIsOver();
            if (!isRunning){
                window.end();
            }
        }
    }

}