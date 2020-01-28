import javax.swing.JPanel;
import java.awt.*;

public class Board extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int screenWidth;
    private final int DIMS = 8;
    private final int numPieces = 12;
    private int TILE_WIDTH;
    private int pieceWidth;
    private int padding = 10;
    private int tilePadding = 5;
    private int hTilePad = tilePadding / 2;
    public Piece[] redPieces;
    public Piece[] bluePieces;
    public Piece nullPiece;
    public int[] selectedPiece = {-1,-1};
    public boolean gameStarted = false;

    public String str(int i) {
        return Integer.toString(i);
    }

    public void printPieces(String side) {
        System.out.println();
        if (side.contains("blue")) {
            System.out.print("BLUE: ");
            for (Piece p : bluePieces) {
                System.out.print(str(p.row) + "," + str(p.col));
                System.out.print(" | ");
            }
        } else if (side.contains("red")) {
            System.out.print("RED: ");
            for (Piece p : redPieces) {
                System.out.print(str(p.row) + "," + str(p.col));
                System.out.print(" | ");
            }
        }
        System.out.println();
    }

    public void reset(){
        gameStarted = false;
        repaint();
    }

    public Board(int pscreenWidth) {
        redPieces = new Piece[numPieces];
        bluePieces = new Piece[numPieces];
        this.screenWidth = pscreenWidth - padding * 2;
        this.TILE_WIDTH = (this.screenWidth / this.DIMS);
        this.pieceWidth = TILE_WIDTH - tilePadding;
        nullPiece = new Piece(-1, -1, -1, -1, -1, -1, Color.ORANGE);
        repaint();
    }

    public Piece choosePieceAt(String side, int row, int col) {
        if (side.contains("blue")) {
            for (Piece p : bluePieces) {
                if (p.row == row && p.col == col) {
                    selectPiece(p.row,p.col);
                    return p;
                }
            }
        }
        if (side.contains("red")) {
            for (Piece p : redPieces) {
                if (p.row == row && p.col == col) {
                    selectPiece(p.row,p.col);
                    return p;
                }
            }
        }
        System.out.println("NO PIECE AT LOCATION: (R: " + Integer.toString(row) + ", C: " + Integer.toString(col) + ")");
        return nullPiece;
    }

    public void selectPiece(int row, int col){
        selectedPiece[0] = row;
        selectedPiece[1] = col;
    }

    public void movePiece(Piece p, int destrow, int destcol) {
        p.movePieceTo(destrow, destcol);
        repaint();
    }

    public void paint(Graphics g) {
        g.setColor(Color.GRAY);
        ((Graphics2D) g).setStroke(new BasicStroke(padding * 2));
        g.drawRect(0, 0, screenWidth + padding * 2, screenWidth + padding * 2);
        g.setColor(Color.black);
        int x = padding, y = padding;
        boolean black = true;
        int nPiece = 0;
        for (int r = 0; r < DIMS; r++) {
            for (int c = 0; c < DIMS; c++) {
                if (black) {
                    g.fillRect(x, y, TILE_WIDTH, TILE_WIDTH);

                    if (!gameStarted) {
                        if (nPiece < numPieces) {
                            redPieces[nPiece] = new Piece(pieceWidth, r, c, DIMS, TILE_WIDTH, padding + hTilePad,
                                    Color.red);
                            bluePieces[nPiece] = new Piece(pieceWidth, (DIMS - 1 - r), (DIMS - 1 - c), DIMS, TILE_WIDTH,
                                    padding + hTilePad, Color.blue);
                            nPiece++;
                        }

                    }
                }

                x += TILE_WIDTH;
                black = !black;
            }
            y += TILE_WIDTH;
            x = padding;
            black = !black;

        }
        this.gameStarted = true;

        Piece p;
        for (int i = 0; i < numPieces; i++) {
            p = redPieces[i];
            p.render(g);

            p = bluePieces[i];
            p.render(g);
        }
        
        printPieces("red");
        printPieces("blue");
    }
}