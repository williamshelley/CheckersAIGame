import javax.swing.JPanel;
import java.awt.*;
import java.util.*;

public class Board extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final double SCREEN_WIDTH;
    private final double SCREEN_HEIGHT;

    private static final int BOARD_DIM = 8;
    private final int NUM_PIECES_PER_SIDE = 12;

    private final int NUM_COLSX = BOARD_DIM;
    private final int NUM_ROWSY = BOARD_DIM;
    private static final double BOARD_PADDING_PERCENT = 0.05;
    private final double BOARD_H_PADDING;
    private final double BOARD_V_PADDING;
    private final double BOARD_WIDTH;
    private final double BOARD_HEIGHT;

    private static final double TILE_INNER_PADDING_PERCENT = 0.05;
    private final double TILE_H_INNER_PADDING;
    private final double TILE_V_INNER_PADDING;
    private final double TILE_WIDTH;
    private final double TILE_HEIGHT;

    private final Color blue = Color.blue;
    private final Color red = Color.red;

    private Piece[][] board;
    private Tile[][] tiles;
    private boolean gameStarted;
    private Piece nullPiece;
    private Tile nullTile;

    public Board(int windowWidth, int windowHeight) {
        SCREEN_WIDTH = (double) windowWidth;
        SCREEN_HEIGHT = (double) windowHeight;
        BOARD_H_PADDING = SCREEN_WIDTH * BOARD_PADDING_PERCENT;
        BOARD_V_PADDING = SCREEN_HEIGHT * BOARD_PADDING_PERCENT;
        BOARD_WIDTH = SCREEN_WIDTH - BOARD_H_PADDING * 2.0;
        BOARD_HEIGHT = SCREEN_HEIGHT - BOARD_V_PADDING * 2.0;
        TILE_WIDTH = BOARD_WIDTH / (double) NUM_COLSX;
        TILE_HEIGHT = BOARD_HEIGHT / (double) NUM_ROWSY;
        TILE_H_INNER_PADDING = TILE_WIDTH * TILE_INNER_PADDING_PERCENT;
        TILE_V_INNER_PADDING = TILE_HEIGHT * TILE_INNER_PADDING_PERCENT;
        board = new Piece[NUM_COLSX][NUM_ROWSY];
        tiles = new Tile[NUM_COLSX][NUM_ROWSY];
        gameStarted = false;
        nullPiece = new Piece(null, -1, -1, 0, 0, 0, 0, 0, 0);
        nullTile = new Tile(null, 0, 0, 0, 0, 0, 0);
        for (int x = 0; x < NUM_COLSX; x++) {
            for (int y = 0; y < NUM_ROWSY; y++) {
                board[x][y] = nullPiece;
                tiles[x][y] = nullTile;
            }
        }
        repaint();
    }

    private String str(int i) {
        return Integer.toString(i);
    }

    private boolean isEvenTile(int x, int y) {
        return (x + y) % 2 == 0;
    }

    private int jumpLandingPosX(Piece jumper, Piece station) {
        int dirX = station.getxcol() - jumper.getxcol();
        return dirX * 2 + jumper.getxcol();
    }

    private int jumpLandingPosY(Piece jumper, Piece station) {
        int dirY = station.getyrow() - jumper.getyrow();
        return dirY * 2 + jumper.getyrow();
    }

    private boolean inBounds(int i) {
        return (i >= 0 && i < BOARD_DIM);
    }

    private boolean checkForJump(Piece jumper, Piece station) {
        int leapX = jumpLandingPosX(jumper, station);
        int leapY = jumpLandingPosY(jumper, station);
        if (inBounds(leapX) && inBounds(leapY)) {
            if (board[leapX][leapY].isNull()) {
                return true;
            }
        }
        return false;
    }

    private boolean pointInList(ArrayList<Point[]> moves, Point point){
        for (Point[] p : moves){
            if (p[0].equals(point)){
                return true;
            }
        }
        return false;
    }

    private boolean validateMove(ArrayList<Point[]> moves, Piece p, int xcol, int yrow){
        Point point = new Point(xcol,yrow);
        boolean bounded = inBounds(xcol) && inBounds(yrow);
        boolean validMove = !p.isNull() && bounded 
                        && isEvenTile(xcol,yrow) 
                        && pointInList(moves, point);
        return validMove;
    }

    private boolean removePiece(ArrayList<Point[]> moves, int xcol, int yrow){
        Point pxy = new Point(xcol,yrow);
        for (Point[] p : moves){
            if (p[0].equals(pxy)){
                if (p[1].x >= 0 && p[1].y >= 0){
                    board[p[1].x][p[1].y] = nullPiece;
                    return true;
                }
            }
        }
        return false;
    }

    private String sideToString(Color color){
        if (color == Color.red){
            return "Red";
        }
        else if (color == Color.blue){
            return "Blue";
        }
        return color.toString();
    }

    private void resetBoard(Graphics g) {
        int blueCount = 0;
        int redCount = 0;
        for (int y = 0; y < NUM_ROWSY; y++) {
            for (int x = 0; x < NUM_COLSX; x++) {
                int lastX = BOARD_DIM - x - 1;
                int lastY = BOARD_DIM - y - 1;
                boolean evenSpace = isEvenTile(x, y);
                if (evenSpace) {
                    tiles[x][y] = new Tile(Color.black, x, y, TILE_WIDTH, TILE_HEIGHT, BOARD_H_PADDING,
                            BOARD_V_PADDING);
                } else {
                    tiles[x][y] = new Tile(Color.gray, x, y, TILE_WIDTH, TILE_HEIGHT, BOARD_H_PADDING, BOARD_V_PADDING);
                }
                if (redCount < NUM_PIECES_PER_SIDE && evenSpace) {
                    board[lastX][lastY] = new Piece(red, lastX, lastY, TILE_WIDTH, TILE_HEIGHT, TILE_H_INNER_PADDING,
                            TILE_V_INNER_PADDING, BOARD_H_PADDING, BOARD_V_PADDING);
                    redCount++;
                }
                if (blueCount < NUM_PIECES_PER_SIDE && evenSpace) {
                    board[x][y] = new Piece(blue, x, y, TILE_WIDTH, TILE_HEIGHT, TILE_H_INNER_PADDING,
                            TILE_V_INNER_PADDING, BOARD_H_PADDING, BOARD_V_PADDING);
                    blueCount++;
                }

            }
        }
        gameStarted = true;
    }

    public void printPossibleMoves(int xcol, int yrow){
        ArrayList<Point[]> possibleMoves = getPossibleMoves(xcol, yrow);
        System.out.print("| ");
        for (Point[] p : possibleMoves){
            System.out.print("<");
            p[0].print();
            System.out.print("> <");
            p[1].print();
            System.out.print("> | ");
        }
        System.out.println();
    }

    //index 0 is all possible points reachable
    //index 1 are initialized to null, filled if there is a jump possibility with the stationary point
    public ArrayList<Point[]> getPossibleMoves(int xcol, int yrow) {
        Piece p = board[xcol][yrow];
        ArrayList<Point[]> possibleMoves = new ArrayList<>();
        int lookAheadX = p.getxcol() + 2;
        int lookAheadY = p.getyrow() + 2;
        int lookBehindX = p.getxcol() - 1;
        int lookBehindY = p.getyrow() - 1;

        if (p.getxcol() <= 0 || lookAheadX >= NUM_COLSX) {
            lookBehindX = 0;
        } else if (p.getxcol() >= NUM_COLSX || lookAheadX >= NUM_COLSX) {
            lookAheadX = NUM_COLSX - 1;
        }

        if (p.getyrow() <= 0 || lookAheadY <= 0) {
            lookBehindY = 0;
        } else if (p.getyrow() >= NUM_ROWSY || lookAheadY >= NUM_ROWSY) {
            lookAheadY = NUM_ROWSY - 1;
        }

        for (int x = lookBehindX; x < lookAheadX; x++) {
            for (int y = lookBehindY; y < lookAheadY; y++) {
                Piece dest = board[x][y];
                Point[] points = new Point[2];
                points[1] = new Point(-1,-1);
                if (dest.isNull() && isEvenTile(x, y)) {
                    points[0] = new Point(x,y);
                    possibleMoves.add(points);
                    
                } else {
                    if (checkForJump(p, dest) && p.isEnemyOf(dest)) {
                        int landingX = jumpLandingPosX(p, dest);
                        int landingY = jumpLandingPosY(p, dest);
                        points[0] = new Point(landingX,landingY);
                        points[1] = new Point(dest);
                        possibleMoves.add(points);
                    }
                }
            }
        }
        return possibleMoves;
    }

    public void move(Piece p, int xcol, int yrow) {
        ArrayList<Point[]> possibleMoves = getPossibleMoves(p.getxcol(), p.getyrow());
        boolean validMove = validateMove(possibleMoves,p,xcol,yrow);
        boolean removedPiece = false;
        if (validMove){
            removedPiece = removePiece(possibleMoves, xcol, yrow);
            board[xcol][yrow] = p;
            board[p.getxcol()][p.getyrow()] = nullPiece;
            board[xcol][yrow].move(xcol, yrow);
            repaint();
            if (removedPiece){
                System.out.println(sideToString(p.getSide()) + " goes again!");
            }
        }
    }

    public void paint(Graphics g) {
        if (!gameStarted) {
            this.resetBoard(g);
        }
        for (int x = 0; x < NUM_COLSX; x++) {
            for (int y = 0; y < NUM_ROWSY; y++) {
                tiles[x][y].render(g);
            }
        }
        for (int x = 0; x < NUM_COLSX; x++) {
            for (int y = 0; y < NUM_ROWSY; y++) {
                board[x][y].render(g);
            }
        }
    }

    public void update() {
        repaint();
    }

    public Piece[][] getBoard() {

        return this.board;
    }

    public void printBoard() {
        for (int y = 0; y < NUM_ROWSY; y++) {
            System.out.print("| ");
            for (int x = 0; x < NUM_COLSX; x++) {
                Piece p = board[x][y];
                if (p.getxcol() < 0 || p.getyrow() < 0) {
                    System.out.print("-,- | ");
                } else {
                    System.out.print(Integer.toString(p.getxcol()) + "," + Integer.toString(p.getyrow()) + " | ");
                }

            }
            System.out.println();
        }
        System.out.println("\n");
    }
} 