import javax.swing.JPanel;
import java.awt.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

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
    private Board lastMoveBoard;
    private Tile[][] tiles;
    private boolean gameStarted;
    private Piece nullPiece;
    private Tile nullTile;

    private boolean redTurn;
    private boolean hasMovedThisTurn;

    private int redScore;
    private int blueScore;
    private boolean gameOver;

    public Board(Board b) {
        gameOver = b.gameOver;
        redScore = b.redScore;
        blueScore = b.blueScore;
        redTurn = b.redTurn;
        hasMovedThisTurn = b.hasMovedThisTurn;
        gameStarted = true;
        SCREEN_WIDTH = (double) b.SCREEN_WIDTH;
        SCREEN_HEIGHT = (double) b.SCREEN_HEIGHT;
        BOARD_H_PADDING = SCREEN_WIDTH * BOARD_PADDING_PERCENT;
        BOARD_V_PADDING = SCREEN_HEIGHT * BOARD_PADDING_PERCENT;
        BOARD_WIDTH = SCREEN_WIDTH - BOARD_H_PADDING * 2.0;
        BOARD_HEIGHT = SCREEN_HEIGHT - BOARD_V_PADDING * 2.0;
        TILE_WIDTH = BOARD_WIDTH / (double) NUM_COLSX;
        TILE_HEIGHT = BOARD_HEIGHT / (double) NUM_ROWSY;
        TILE_H_INNER_PADDING = TILE_WIDTH * TILE_INNER_PADDING_PERCENT;
        TILE_V_INNER_PADDING = TILE_HEIGHT * TILE_INNER_PADDING_PERCENT;
        board = new Piece[NUM_COLSX][NUM_ROWSY];
        nullPiece = new Piece(null, -1, -1, 0, 0, 0, 0, 0, 0);
        tiles = b.tiles;
        nullTile = new Tile(null, 0, 0, 0, 0, 0, 0);
        for (int x = 0; x < NUM_COLSX; x++) {
            for (int y = 0; y < NUM_ROWSY; y++) {
                board[x][y] = new Piece(b.board[x][y]);
            }
        }
    }

    public boolean gameIsOver() {
        return gameOver;
    }

    public Board(int windowWidth, int windowHeight) {
        gameOver = false;
        redScore = 0;
        blueScore = 0;
        redTurn = true;
        hasMovedThisTurn = false;
        gameStarted = false;
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
        if (!jumper.isEnemyOf(station)) {
            return false;
        }
        int leapX = jumpLandingPosX(jumper, station);
        int leapY = jumpLandingPosY(jumper, station);
        if (inBounds(leapX) && inBounds(leapY)) {
            if (board[leapX][leapY].isNull()) {
                return true;
            }
        }
        return false;
    }

    private boolean pointInList(ArrayList<Point[]> moves, Point point) {
        for (Point[] p : moves) {
            if (p[0].equals(point)) {
                return true;
            }
        }
        return false;
    }

    private boolean pointPairInList(ArrayList<Point[]> moves, Point[] points) {
        for (Point[] p : moves) {
            if (p[0].equals(points[0]) && p[1].equals(points[1])) {
                return true;
            }
        }
        return false;
    }

    private boolean validateMove(ArrayList<Point[]> moves, Piece p, int xcol, int yrow) {
        Point point = new Point(xcol, yrow);
        boolean bounded = inBounds(xcol) && inBounds(yrow);
        boolean validMove = !p.isNull() && bounded && isEvenTile(xcol, yrow) && pointInList(moves, point);
        return validMove;
    }

    private boolean removePiece(ArrayList<Point[]> moves, int xcol, int yrow) {
        Point pxy = new Point(xcol, yrow);
        for (Point[] p : moves) {
            if (p[0].equals(pxy)) {
                if (p[1].x >= 0 && p[1].y >= 0) {
                    board[p[1].x][p[1].y] = nullPiece;
                    moves.remove(p);
                    return true;
                }
            }
        }
        return false;
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


    private void updateScores(Piece activePiece) {
        if (activePiece.getSide() == Color.blue) {
            blueScore++;
        } else if (activePiece.getSide() == Color.red) {
            redScore++;
        }
        if (blueScore >= NUM_PIECES_PER_SIDE) {
            blueWins();
        } else if (redScore >= NUM_PIECES_PER_SIDE) {
            redWins();
        }
    }

    private void blueWins() {
        System.out.println("Blue Wins!");
        setGameOver();
    }

    private void redWins() {
        System.out.println("Red Wins!");
        setGameOver();
    }

    private void setGameOver() {
        gameOver = true;
    }

    private void startNewTurn() {
        hasMovedThisTurn = false;
    }

    public String boardToString() {
        String res = "\n";
        for (int y = 0; y < NUM_ROWSY; y++) {
            res += "| ";
            for (int x = 0; x < NUM_COLSX; x++) {
                Piece p = board[x][y];
                if (p.isNull()) {
                    res += p.toString() + " | ";
                } else {
                    res += p.toString() + " | ";
                }

            }
            res += "\n\n";
        }
        res += "\n\n";
        return res;
    }

    private String sideToString(Color color) {
        if (color == Color.red) {
            return "Red";
        } else if (color == Color.blue) {
            return "Blue";
        }
        return color.toString();
    }

    public void printPossibleMoves(int xcol, int yrow) {
        ArrayList<Point[]> possibleMoves = getPossibleMoves(xcol, yrow);

        System.out.print("| ");
        for (Point[] p : possibleMoves) {
            System.out.print("<");
            p[0].print();
            System.out.print("> <");
            p[1].print();
            System.out.print("> | ");
        }
        System.out.println();
    }


    // index 0 is all possible points reachable
    // index 1 are initialized to null, filled if there is a jump possibility with
    // the stationary point
    public ArrayList<Point[]> getPossibleMoves(int xcol, int yrow) {
        Piece p = board[xcol][yrow];
        ArrayList<Point[]> possibleMoves = new ArrayList<>();

        if (p.isPawn()) {
            possibleMoves = getPawnMoves(xcol, yrow);

        } else {
            if (p.isQueen()) {
                possibleMoves = getQueenMoves(xcol, yrow);
            }
        }
        if (hasMovedThisTurn) {
            possibleMoves = removeNonJumps(possibleMoves);
        }
        return possibleMoves;
    }


    //index 0 of each element is the target piece to move
    //index 1 of each element is the destination
    public void addMovesToList(ArrayList<Point[]> moves, int xcol, int yrow) {
        Piece p = board[xcol][yrow];
        if (p.isPawn()) {
            ArrayList<Point[]> m = getPawnMoves(xcol, yrow);
            for (Point[] points : m){
                Point[] pt = new Point[2];
                pt[0] = new Point(xcol,yrow);
                pt[1] = points[0];
                moves.add(pt);
            }

        } else {
            if (p.isQueen()) {
                ArrayList<Point[]> m = getQueenMoves(xcol, yrow);
                for (Point[] points : m){
                    moves.add(points);
                }
            }
        }
    }

    public ArrayList<Point[]> getAllPossibleMovesForSide(Color side) {
        ArrayList<Point[]> moves = new ArrayList<>();
        for (Piece[] pieces : board) {
            for (Piece piece : pieces) {
                if (piece.getSide() == side) {
                    addMovesToList(moves, piece.getxcol(), piece.getyrow());
                }
            }
        }
        return moves;
    }

    public void printAllPossibleMovesOnSide(Color side){
        ArrayList<Point[]> moves = getAllPossibleMovesForSide(Color.red);
        System.out.println();
        for (Point[] p : moves) {
            System.out.print("<");
            p[0].print();
            System.out.print("> <");
            p[1].print();
            System.out.print("> | ");
        }
        System.out.println("\n");
    }

    private void addToQueenMoves(ArrayList<Point[]> moves, Piece jumper, int xdir, int ydir) {
        int lx = jumper.getxcol() + xdir;
        int ly = jumper.getyrow() + ydir;
        int numEnemies = 0;
        Point lastPoint = new Point(-1, -1);
        while (inBounds(lx) && inBounds(ly)) {
            Piece p = board[lx][ly];
            if (p.isNull()) {
                Point[] points = new Point[2];
                Piece dest = nullPiece;
                if (inBounds(lastPoint.x) && inBounds(lastPoint.y)) {
                    dest = board[lastPoint.x][lastPoint.y];
                }
                points[0] = new Point(lx, ly);
                points[1] = new Point(-1, -1);
                if (!lastPoint.isNull() && dest.isEnemyOf(jumper)) {
                    points[1] = new Point(lastPoint.x, lastPoint.y);
                }
                if (!pointPairInList(moves, points) && numEnemies >= 0 && numEnemies <= 1) {
                    moves.add(points);
                }
            }
            if (p.isEnemyOf(jumper)) {
                lastPoint = new Point(lx, ly);
                numEnemies++;
            }
            if (p.isAllyOf(jumper) || numEnemies > 1) {
                return;
            }
            lx += xdir;
            ly += ydir;
        }
    }

    private void addToPawnMoves(ArrayList<Point[]> moves, Piece origin, int xcol, int yrow) {
        Piece p = origin;
        Piece dest = board[xcol][yrow];
        Point[] points = new Point[2];
        points[1] = new Point(-1, -1);
        int distanceX = xcol - p.getxcol();
        int distanceY = yrow - p.getyrow();
        int absDist = Math.abs(distanceX) + Math.abs(distanceY);
        boolean redDirection = (p.getSide() == Color.red && p.getyrow() >= yrow);
        boolean blueDirection = (p.getSide() == Color.blue && p.getyrow() <= yrow);
        boolean validDirection = p.isQueen() || (p.isPawn() && (redDirection || blueDirection));

        if (dest.isNull() && isEvenTile(xcol, yrow) && ((absDist <= 2 && validDirection))) {
            points[0] = new Point(xcol, yrow);
            points[1] = new Point(-1, -1);
            if (!pointPairInList(moves, points)) {
                moves.add(points);
            }
        } else {
            if (checkForJump(p, dest) && (validDirection)) {
                int landingX = jumpLandingPosX(p, dest);
                int landingY = jumpLandingPosY(p, dest);
                if (inBounds(landingX) && inBounds(landingY)) {

                    Piece landing = board[landingX][landingY];
                    if (!landing.isNull()) {
                        return;
                    }
                    points[0] = new Point(landingX, landingY);
                    points[1] = new Point(dest);
                    if (!pointPairInList(moves, points)) {
                        moves.add(points);
                    }
                }
            }
        }
    }

    public ArrayList<Point[]> getPawnMoves(int xcol, int yrow) {
        Piece p = board[xcol][yrow];
        ArrayList<Point[]> moves = new ArrayList<>();

        int lookAheadX = p.getxcol() + 2;
        int lookAheadY = p.getyrow() + 2;
        int lookBehindX = p.getxcol() - 1;
        int lookBehindY = p.getyrow() - 1;

        if (p.getxcol() <= 0 || lookAheadX <= 0) {
            lookBehindX = 0;
        } else if (p.getxcol() >= NUM_COLSX || lookAheadX >= NUM_COLSX) {
            lookAheadX = NUM_COLSX;
        }

        if (p.getyrow() <= 0 || lookAheadY <= 0) {
            lookBehindY = 0;
        } else if (p.getyrow() >= NUM_ROWSY || lookAheadY >= NUM_ROWSY) {
            lookAheadY = NUM_ROWSY;
        }

        for (int x = lookBehindX; x < lookAheadX; x++) {
            for (int y = lookBehindY; y < lookAheadY; y++) {
                if (!pointInList(moves, new Point(p.getxcol(), p.getyrow()))) {
                    addToPawnMoves(moves, p, x, y);
                }
            }
        }
        return moves;
    }

    private ArrayList<Point[]> getQueenMoves(int xcol, int yrow) {
        Piece origin = board[xcol][yrow];
        ArrayList<Point[]> moves = new ArrayList<>();
        addToQueenMoves(moves, origin, 1, -1);
        addToQueenMoves(moves, origin, 1, 1);
        addToQueenMoves(moves, origin, -1, -1);
        addToQueenMoves(moves, origin, -1, 1);
        return moves;
    }

    public void undoLastMove() {
        this.board = lastMoveBoard.board;
        this.redTurn = lastMoveBoard.redTurn;
        this.hasMovedThisTurn = lastMoveBoard.hasMovedThisTurn;
        this.lastMoveBoard = lastMoveBoard.lastMoveBoard;
        System.out.println("undid move");
        repaint();
    }

    private ArrayList<Point[]> removeNonJumps(ArrayList<Point[]> moves) {
        ArrayList<Point[]> result = new ArrayList<>();
        for (Point[] points : moves) {
            if (!points[1].isNull()) {
                result.add(points);
            }
        }
        return result;
    }

    // when reach end of rows on either end, turn said piece into queen
    public void move(Piece p, int xcol, int yrow) {
        boolean correctPieceForTurn = (redTurn && p.getSide() == Color.red)
                || (redTurn && p.getQueenColor() == Color.magenta) || (!redTurn && p.getSide() == Color.blue)
                || (!redTurn && p.getQueenColor() == Color.green);
        if (correctPieceForTurn) {
            ArrayList<Point[]> possibleMoves = getPossibleMoves(p.getxcol(), p.getyrow());

            if (hasMovedThisTurn) {
                possibleMoves = removeNonJumps(possibleMoves);
            }
            boolean validMove = validateMove(possibleMoves, p, xcol, yrow);
            boolean removedPiece = false;
            if (validMove) {
                this.lastMoveBoard = new Board(this);
                hasMovedThisTurn = true;
                removedPiece = removePiece(possibleMoves, xcol, yrow);
                board[xcol][yrow] = p;
                board[p.getxcol()][p.getyrow()] = nullPiece;
                board[xcol][yrow].move(xcol, yrow);
                if ((p.getSide() == Color.blue && yrow == NUM_ROWSY - 1) || (p.getSide() == Color.red && yrow == 0)) {
                    board[xcol][yrow].promote();
                }
                if (removedPiece) {
                    updateScores(p);
                    possibleMoves = getPossibleMoves(xcol, yrow);
                    possibleMoves = removeNonJumps(possibleMoves);
                    if (!possibleMoves.isEmpty()) {
                        System.out.println("\n" + sideToString(p.getSide()) + " goes again!");
                    } else {
                        endTurn();
                    }
                } else {
                    endTurn();
                }
                repaint();
            }
        } else {
            System.out.println("Not your turn!");
        }
    }

    public boolean getHasMovedThisTurn() {
        return hasMovedThisTurn;
    }

    public Color currentTurn() {
        if (redTurn) {
            return Color.red;
        } else {
            return Color.blue;
        }
    }

    public void endTurn() {
        if (hasMovedThisTurn) {
            redTurn = !redTurn;
            startNewTurn();
            System.out.println("Ended Turn!");
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

    public void promote(int xcol, int yrow) {
        if (!board[xcol][yrow].isNull()) {
            board[xcol][yrow].promote();
            this.update();
        }
    }

    public Piece[][] getBoard() {
        return this.board;
    }

    public void printBoard() {

        System.out.println();
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
            System.out.println('\n');
        }
        System.out.println("\n");
    }
}