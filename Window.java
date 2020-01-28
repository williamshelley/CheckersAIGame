import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.*;

public class Window extends JFrame{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static int BOARD_DIMS = 8;
    private Board board;
    private static int heightPadding = 10;
    public static int WINDOW_WIDTH=500;
    public static int WINDOW_HEIGHT = WINDOW_WIDTH+heightPadding*2;

    
    public Window(int width, int height, String title){
        setTitle(title);
		setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1,1,0,0));
        board = new Board(WINDOW_WIDTH);
        add(board);
        setVisible(true);
    }

    public void createBoard(int rows, int cols){


    }

    public void updateBoard(){

    }

    public void end(){
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Window window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "CHECKERS");
        boolean isRunning = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
        while (isRunning){
            TimeUnit.SECONDS.sleep(1);
            System.out.println();

            System.out.print("Input: ");
            String name = reader.readLine();
            if (name.equalsIgnoreCase("reset")){
                window.board.reset();
            }
            else if (name.equalsIgnoreCase("Q") || name.equalsIgnoreCase("Quit") || name.equalsIgnoreCase("exit")){
                isRunning = false;
                window.end();
            }
            else if (name.contains("move")){
                System.out.print("Side (Red/Blue): ");
                String side = reader.readLine();
                while (!side.contains("blue") && !side.contains("red")){
                    System.out.print("Invalid Side. Choose Side (Red/Blue): ");
                    side = reader.readLine();
                }
                System.out.print(side + " Row,Column: ");
                String[] rowCol = reader.readLine().split(",");
                int row = Integer.parseInt(rowCol[0]);
                int col = Integer.parseInt(rowCol[1]);
                boolean validPiece = (window.board.choosePieceAt(side, row, col).col >= 0);
                
                while (!validPiece){
                    window.board.printPieces(side);
                    System.out.print("Invalid Piece, choose again." + side + " Row,Column: ");
                    rowCol = reader.readLine().split(",");
                    row = Integer.parseInt(rowCol[0]);
                    col = Integer.parseInt(rowCol[1]);
                    validPiece = (window.board.choosePieceAt(side, row, col).col >= 0);
                }
                Piece targetPiece = window.board.choosePieceAt(side, row, col);
                System.out.print("Choose destination Row,Column: ");
                String[] destination = reader.readLine().split(",");
                int destRow = Integer.parseInt(destination[0]);
                int destCol = Integer.parseInt(destination[1]);
                while (window.board.choosePieceAt(side, destRow, destCol).row >=0 || destRow < 0 || destCol > BOARD_DIMS){
                    System.out.print("Invalid Destination, choose again." + side + " Row,Column: ");
                    destination = reader.readLine().split(",");
                    destRow = Integer.parseInt(rowCol[0]);
                    destRow = Integer.parseInt(rowCol[1]);
                }
                window.board.movePiece(targetPiece, destRow, destCol);
            }
        }
    }
    
}

