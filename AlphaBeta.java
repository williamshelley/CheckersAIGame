import java.awt.*;
import java.util.*;

public class AlphaBeta {

    boolean renderEachMove = true;
    boolean renderBestMove = true;

    Color side;
    int recursiveDepth = 0;
    int maxRecurse = 24;
    int maxBestMove = -1;

    public Color getSide(){
        return this.side;
    }

    public AlphaBeta(Color side) {
        this.side = side;
    }

    public void play(Board b) throws InterruptedException {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        recursiveDepth = 0;
        maxBestMove = -1;
        maxValue(b, alpha, beta);
    }

    // opponent's move
    public int minValue(Board b, int alpha, int beta) throws InterruptedException {
        if (b.gameIsOver() || recursiveDepth++ > maxRecurse) {
            return b.getValue();
        }
        Color opposing = b.opposingColor(this.side);
        ArrayList<Point[]> possibleMoves = b.getAllPossibleMovesForSide(opposing);
        int moveIndex = 0;
        while (alpha < beta && moveIndex < possibleMoves.size()){
            Point[] move = possibleMoves.get(moveIndex);
            Piece startLoc = b.getPiece(move[2]);
            b.move(startLoc, move[0].x, move[0].y,renderEachMove);
            int value;
            if (b.currentTurn() == opposing) {
                value = this.minValue(b, alpha, beta);
            } else {
                value = this.maxValue(b, alpha, beta);
            }
            b.undoLastMove();

            if (value < beta) {
                beta = value;
            }
            moveIndex++;
        }
        return beta;
    }

    // alpha beta move
    // 0 destination, 1 piece taken, 2 source location
    public int maxValue(Board b, int alpha, int beta) throws InterruptedException {
        if (b.gameIsOver() || recursiveDepth++ > maxRecurse) {
            return b.getValue();
        }
        ArrayList<Point[]> possibleMoves = b.getAllPossibleMovesForSide(this.side);
        int moveIndex = 0;
        while (alpha < beta && moveIndex < possibleMoves.size()){
            Point[] move = possibleMoves.get(moveIndex);
            Piece startLoc = b.getPiece(move[2]);
            b.move(startLoc, move[0].x, move[0].y,renderEachMove);
            int value;
            if (b.currentTurn() == this.side) {
                value = this.maxValue(b, alpha, beta);
            } else {
                value = this.minValue(b, alpha, beta);
            }
            b.undoLastMove();

            if (value > alpha) {
                alpha = value;
                maxBestMove = moveIndex;
            }
            moveIndex++;
        }
        Point[] move = possibleMoves.get(maxBestMove);
        Piece startLoc = b.getPiece(move[2]);
        b.move(startLoc, move[0].x,move[0].y,renderBestMove);
        return alpha;
    }
}