import javax.swing.JPanel;
import java.awt.*;
import java.util.*;

public class Board extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final double SCREEN_WIDTH = Window.WINDOW_WIDTH;
    private static final double SCREEN_HEIGHT = Window.WINDOW_HEIGHT;

    private static final int BOARD_DIM = 8;
    private static final int NUM_PIECES_PER_SIDE = 12;

    private static final int NUM_COLSX = BOARD_DIM;
    private static final int NUM_ROWSY = BOARD_DIM;
    private static final double BOARD_PADDING_PERCENT = 0.05;
    private static final double BOARD_H_PADDING = SCREEN_WIDTH * BOARD_PADDING_PERCENT;
    private static final double BOARD_V_PADDING = SCREEN_HEIGHT * BOARD_PADDING_PERCENT;
    private static final double BOARD_WIDTH  = SCREEN_WIDTH - BOARD_H_PADDING * 2.0;
    private static final double BOARD_HEIGHT = SCREEN_HEIGHT - BOARD_V_PADDING * 2.0;

    private static final double TILE_WIDTH = BOARD_WIDTH / (double) NUM_COLSX;
    private static final double TILE_HEIGHT = BOARD_HEIGHT / (double) NUM_ROWSY;;
    private static final double TILE_INNER_PADDING_PERCENT = 0.05;
    private static final double TILE_H_INNER_PADDING = TILE_WIDTH * TILE_INNER_PADDING_PERCENT;
    private static final double TILE_V_INNER_PADDING = TILE_HEIGHT * TILE_INNER_PADDING_PERCENT;

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

    private boolean gameOver;
    private boolean START_RED = true;

    private boolean PRINT_LOG = false;

    public Board(Board b) {
        gameOver = b.gameOver;
        redTurn = b.redTurn;
        hasMovedThisTurn = b.hasMovedThisTurn;
        gameStarted = true;
        board = new Piece[NUM_COLSX][NUM_ROWSY];
        nullPiece = new Piece(null, -1, -1, 0, 0, 0, 0, 0, 0);
        tiles = b.tiles;
        nullTile = new Tile(null, 0, 0, 0, 0, 0, 0);
        for (int x = 0; x < NUM_COLSX; x++) {
            for (int y = 0; y < NUM_ROWSY; y++) {
                board[x][y] = new Piece(b.board[x][y]);
            }
        }
        lastMoveBoard = b.lastMoveBoard;
    }

    
    public Board() {
        gameOver = false;
        redTurn = START_RED;
        hasMovedThisTurn = false;
        gameStarted = false;
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
        lastMoveBoard = this;
        resetBoard();
        repaint();
    }
    
    public void results(){
        int numRed = numPiecesOnSide(Color.red);
        int numBlue = numPiecesOnSide(Color.blue);
        if (numBlue > numRed) {
            blueWins();
        } else if (numRed > numBlue) {
            redWins();
        } else {
            System.out.println("Draw you idiots!");
        }
    }
    
    public void printList(ArrayList<Point[]> list){
        for (Point[] points : list){
            for (Point p : points){
                p.print();
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    public boolean gameIsOver() throws InterruptedException {
        int numRed = this.numPiecesOnSide(Color.red);
        int numBlue = this.numPiecesOnSide(Color.blue);
        ArrayList<Point[]> movesRed = getAllPossibleMovesForSide(Color.red);
        ArrayList<Point[]> movesBlue = getAllPossibleMovesForSide(Color.blue);
        return (
            numRed <= 0 || numBlue <= 0 
            || (redTurn && movesRed.size() < 1) 
            || (!redTurn && movesBlue.size() < 1)
        );
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

    private boolean removePiece(ArrayList<Point[]> possibles, Piece source, int xcol, int yrow) {
        Point pxy = new Point(xcol, yrow);
        Point srcXY = new Point(source.getxcol(), source.getyrow());
        ArrayList<Point[]> moves = getPossibleMovesForPiece(source.getxcol(), source.getyrow());
        for (Point[] p : moves) {
            if (p[0].equals(pxy) && p[2].equals(srcXY)) {
                if (!p[1].isNull()) {
                    board[p[1].x][p[1].y] = nullPiece;
                    return true;
                }
            }
        }
        return false;
    }

    private void resetBoard() {
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

    public String sideToString(Color color) {
        if (color == Color.red) {
            return "Red";
        } else if (color == Color.blue) {
            return "Blue";
        }
        return color.toString();
    }

    // index 0 is all possible points reachable
    // index 1 are initialized to null, filled if there is a jump possibility with
    // the stationary point
    public ArrayList<Point[]> getPossibleMovesForPiece(int xcol, int yrow) {
        Piece p = board[xcol][yrow];
        ArrayList<Point[]> possibleMoves = new ArrayList<>();

        if (p.isPawn()) {
            possibleMoves = getPawnMoves(xcol, yrow);

        } else if (p.isQueen()) {
            possibleMoves = getQueenMoves(xcol, yrow);
        }
        if (hasMovedThisTurn || jumpsAreAvailable(possibleMoves)) {
            
            possibleMoves = removeNonJumps(possibleMoves);
        }
        return possibleMoves;
    }

    public ArrayList<Point[]> getAllPossibleMovesForSide(Color side) {
        ArrayList<Point[]> moves = new ArrayList<>();
        for (Piece[] pieces : board) {
            for (Piece piece : pieces) {
                if (piece.getSide() == side) {
                    ArrayList<Point[]> m = getPossibleMovesForPiece(piece.getxcol(), piece.getyrow());
                    for (Point[] points : m){
                        moves.add(points);
                    }
                }
            }
        }
        for (Point[] points : moves){
            if (!points[1].isNull()){
                moves = removeNonJumps(moves);
            }
        }
        return moves;
    }

    private void addToQueenMoves(ArrayList<Point[]> moves, Piece jumper, int xdir, int ydir) {
        int lx = jumper.getxcol() + xdir;
        int ly = jumper.getyrow() + ydir;
        int numEnemies = 0;
        Point lastPoint = new Point(-1, -1);
        while (inBounds(lx) && inBounds(ly)) {
            Piece p = board[lx][ly];
            if (p.isNull()) {
                Point[] points = new Point[3];
                Piece dest = nullPiece;
                if (inBounds(lastPoint.x) && inBounds(lastPoint.y)) {
                    dest = board[lastPoint.x][lastPoint.y];
                }
                points[0] = new Point(lx, ly);
                points[1] = new Point(-1, -1);
                points[2] = new Point(jumper.getxcol(),jumper.getyrow());
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
        Point[] points = new Point[3];
        points[1] = new Point(-1, -1);
        points[2] = new Point(p.getxcol(),p.getyrow());
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

    public Piece[][] copyBoard(Piece[][] original){
        Piece[][] res = new Piece[original.length][original[0].length];
        for (int i = 0; i < res.length; i++){
            for (int j = 0; j < res[i].length; j++){
                res[i][j] = original[i][j];
            }
        }
        return res;
    }

    public void undoLastMove() {
        this.board = copyBoard(lastMoveBoard.board);
        this.redTurn = lastMoveBoard.redTurn;
        this.hasMovedThisTurn = lastMoveBoard.hasMovedThisTurn;
        this.lastMoveBoard = new Board(lastMoveBoard.lastMoveBoard);
        this.gameOver = lastMoveBoard.gameOver;
        if (PRINT_LOG){
            System.out.println("Undid last move!");
        }
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

    public void render(){
        repaint();
    }

    public boolean jumpsAreAvailable(ArrayList<Point[]> moves){
        for (Point[] points: moves){
            if (!points[1].isNull()){
                return true;
            }
        }
        return false;
    }

    public void move(Piece p, int xcol, int yrow, boolean render) {
        boolean correctPieceForTurn = (redTurn && p.getSide() == Color.red)
                || (!redTurn && p.getSide() == Color.blue);
        ArrayList<Point[]> possibleMoves = getAllPossibleMovesForSide(currentTurn());
        if (correctPieceForTurn) {
            boolean validMove = validateMove(possibleMoves, p, xcol, yrow);
            boolean removedPiece = false;
            if (validMove) {
                this.lastMoveBoard = new Board(this);
                hasMovedThisTurn = true;
                removedPiece = removePiece(possibleMoves, p, xcol, yrow);
                board[xcol][yrow] = p;
                board[p.getxcol()][p.getyrow()] = nullPiece;
                board[xcol][yrow].move(xcol, yrow);
                if ((p.getSide() == Color.blue && yrow == NUM_ROWSY - 1) || (p.getSide() == Color.red && yrow == 0)) {
                    board[xcol][yrow].promote();
                }
                if (removedPiece) {
                    if (PRINT_LOG){
                        System.out.println("Took Piece!");
                    }
                    possibleMoves = getPossibleMovesForPiece(xcol, yrow);
                    possibleMoves = removeNonJumps(possibleMoves);
                    if (!possibleMoves.isEmpty()) {
                        if (PRINT_LOG){
                            System.out.println("\n" + sideToString(p.getSide()) + " goes again!");
                        }
                    } else {
                        endTurn();
                    }
                } else {
                    endTurn();
                }
                if (render) {
                    repaint();
                }
            }
        } else {
            Color opposingSide = this.opposingColor(this.currentTurn());
            if (PRINT_LOG){
                System.out.println("Not " + this.sideToString(opposingSide) + "'s' turn! It is " + sideToString(this.currentTurn()) + "'s turn!'");
            }
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
        if (hasMovedThisTurn){
            redTurn = !redTurn;
            startNewTurn();
            if (PRINT_LOG){
                System.out.println("Ended Turn!");
            }
        }
    }

    public void paint(Graphics g) {
        if (!gameStarted) {
            this.resetBoard();
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

    public void promote(int xcol, int yrow) {
        if (!board[xcol][yrow].isNull()) {
            board[xcol][yrow].promote();
            repaint();
        }
    }

    public Piece getPiece(int xcol, int yrow){
        return this.board[xcol][yrow];
    }

    public Piece getPiece(Point p){
        return this.board[p.x][p.y];
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

    public void printPossibleMovesForPiece(int xcol, int yrow) {
        ArrayList<Point[]> possibleMoves = getPossibleMovesForPiece(xcol, yrow);

        System.out.print("| ");
        for (Point[] p : possibleMoves) {
            System.out.print("<");
            p[0].print();
            System.out.print("> <");
            p[1].print();
            System.out.print("> <");
            p[2].print();
            System.out.print("> | ");
        }
        System.out.println();
    }



    public void printAllPossibleMovesOnSide(Color side){
        ArrayList<Point[]> moves = getAllPossibleMovesForSide(side);

        System.out.println();
        for (Point[] p : moves) {
            System.out.print("<");
            p[0].print();
            System.out.print("> <");
            p[1].print();
            System.out.print("> <");
            p[2].print();
            System.out.print("> | ");
        }
        System.out.println("\n");
    }


    public int numPiecesOnSide(Color side){
        int count = 0;
        for (Piece[] pieces : board){
            for (Piece p : pieces){
                if (p.getSide() == side){
                    count++;
                }
            }
        }
        return count;
    }

    public int numQueensOnSide(Color side){
        int count = 0;
        for (Piece[] pieces : board){
            for (Piece p : pieces){
                if (p.queenSide() == side){
                    count++;
                }
            }
        }
        return count;
    }

    public Color opposingColor(Color yourSide){
        if (yourSide == Color.red){
            return Color.blue;
        } else{
            return Color.red;
        }
    }

    public int getValue(){
        Color opposing = opposingColor(this.currentTurn());
        int thisSide = numPiecesOnSide(this.currentTurn()) * numQueensOnSide(this.currentTurn());
        int opponent = numPiecesOnSide(opposing) * numQueensOnSide(opposing);
        return opponent - thisSide;
    }
}

