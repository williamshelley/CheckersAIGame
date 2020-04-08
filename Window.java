import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Window extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static int BOARD_DIMS = 8;
    private Board board;
    public static int WINDOW_WIDTH = 850;
    public static int WINDOW_HEIGHT = 850;
    private String movesMade = "";
    private boolean aiMove = true;

    public Window(int width, int height, String title) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 1, 0, 0));
        board = new Board();
        add(board);
        setVisible(true);
    }

    public void end() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("LastGameMoves.txt"));
            writer.write(this.movesMade);
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Failed to write to file!");
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static String getColor(Color c) {
        if (c == Color.red || c == Color.magenta) {
            return "Red";
        } else {
            return "Blue";
        }
    }

    public void playAs(Color side, String select, boolean isRunning) {
        boolean render = true;

        try {
            if (select.contains("q") || select.contains("Q")) {
                isRunning = false;
                this.end();
            } else if (select.contains("promote")) {
                String[] cmd = select.split(",");
                int x = Integer.parseInt(cmd[1]);
                int y = Integer.parseInt(cmd[2]);
                this.board.promote(x, y);
            } else if (select.contains("end")) {
                if (this.board.getHasMovedThisTurn()) {
                    this.board.endTurn();
                }
            } else if (select.contains("getall")) {
                this.board.printAllPossibleMovesOnSide(side);
            } else if (select.contains("undo")) {
                this.board.undoLastMove();
            } else if (select.contains("p") || select.contains("P")) {
                this.board.printBoard();
            } else if (select.contains("c") || select.contains("C")) {
                String[] cmd = select.split(",");
                int x = Integer.parseInt(cmd[1]);
                int y = Integer.parseInt(cmd[2]);
                this.board.printPossibleMovesForPiece(x, y);
            } else if (select.contains(",")) {
                String[] xy = select.split(",");
                int startx = Integer.parseInt(xy[0]);
                int starty = Integer.parseInt(xy[1]);
                int endx = Integer.parseInt(xy[2]);
                int endy = Integer.parseInt(xy[3]);
                Piece piece = this.board.getBoard()[startx][starty];
                if (piece.getSide() == side) {
                    this.board.move(piece, endx, endy, render);
                    aiMove = true;
                } else {
                    System.out.println("It is " + getColor(piece.getEnemySide()) + "'s turn!");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e);
            System.out.println("Invalid Input");
            aiMove = false;
        } catch (NumberFormatException e) {
            System.out.println(e);
            System.out.println("Invalid Input");
            aiMove = false;
        } catch (NullPointerException e) {
            System.out.println(e);
            System.out.println("Invalid Aaction");
            aiMove = false;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Window window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "CHECKERS");
        boolean isRunning = true;
        AlphaBeta redPlayer = new AlphaBeta(Color.red);
        AlphaBeta bluePlayer = new AlphaBeta(Color.blue);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        QLearner blueQLearner = new QLearner(Color.blue);
        boolean PLAYER_VS_AI = false;

        while (isRunning) {
            if (window.board.currentTurn() == Color.red && window.aiMove) {
                if (!PLAYER_VS_AI) {
                    redPlayer.play(window.board);
                }
                if (PLAYER_VS_AI) {
                    window.aiMove = false;
                }
            } else if (window.board.currentTurn() == Color.blue && window.aiMove) {
                //bluePlayer.play(window.board);
                blueQLearner.makeMove(window.board);
                if (PLAYER_VS_AI) {
                    window.aiMove = false;
                }
            }

            if (PLAYER_VS_AI) {
                System.out.print("Q,P,getall,undo,(C,x,y),(sX,sY,dX,dY): ");
                String select = reader.readLine();
                window.movesMade += select + "\n";
                window.playAs(Color.red, select, isRunning);
            }

            if (window.board.gameIsOver()) {
                window.board.results(true);
                isRunning = false;
                window.end();
            }
        }
    }
}