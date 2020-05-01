import java.awt.*;
import java.util.*;
import java.io.*;

public class QLearner {

    public final int numFeatures = 4;
    public final double epsilon = 0.8;
    public final double stepsize = 0.1;
    public Color side;
    public Feature[] features = new Feature[numFeatures];
    public double[] weights = new double[numFeatures];
    public String name = "QLearner";

    public QLearner(Color side) {
        this.side = side;
        readWeightsFromFile();
        features[0] = (Board b) -> (double) b.numPiecesOnSide(side) / 12.0;
        features[1] = (Board b) -> (double) b.numPiecesOnSide(b.opposingColor(side)) / 12.0;
        features[2] = (Board b) -> (double) b.numQueensOnSide(side) / 12.0;
        features[3] = (Board b) -> (double) b.numQueensOnSide(b.opposingColor(side)) / 12.0;
    }

    public void makeMove(Board b) {
        boolean shouldExplore = Math.random() > epsilon;
        ArrayList<Point[]> moves = b.getAllPossibleMovesForSide(this.side);
        if (shouldExplore) {
            int exploreValue = (int) (Math.random() * 10);
            int exploreIndex = exploreValue % moves.size();
            Point[] move = moves.get(exploreIndex);
            Piece startLoc = b.getPiece(move[2]);
            b.move(startLoc, move[0].x, move[0].y, true);
        } else {
            double maxMoveValue = Double.NEGATIVE_INFINITY;
            int maxMoveIndex = -1;
            for (int i = 0; i < moves.size(); i++) {
                Point[] move = moves.get(i);
                Piece startLoc = b.getPiece(move[2]);
                b.move(startLoc, move[0].x, move[0].y, false);
                double valueOfState = getValueOfState(b);
                if (valueOfState > maxMoveValue) {
                    maxMoveValue = valueOfState;
                    maxMoveIndex = i;
                }
                b.undoLastMove();
            }

            Point[] move = moves.get(maxMoveIndex);
            Piece startLoc = b.getPiece(move[2]);
            b.move(startLoc, move[0].x, move[0].y, true);
        }
        updateWeights(b.getLastBoard(), b);
        //writeWeightsToFile();
    }

    public double getReward(Board b) {
        try { 
			if (b.gameIsOver()){
			    Color result = b.results(false);
			    if (result == this.side){
			        return 1;
			    }
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
        return 0;
    }

    public double getValueOfState(Board b){
        double sum = 0;
        for (int i = 0; i < numFeatures; i++){
            sum += weights[i] * features[i].calculate(b) ;
        }
        return sum;
    }

    public void updateWeights(Board oldState, Board newState) {
        for (int i = 0; i < numFeatures; i++){

            weights[i] = weights[i] + stepsize *
                (getReward(newState) + getValueOfState(newState) 
                - getValueOfState(oldState));
        }
    }

    public void readWeightsFromFile(){
        try {
            File file = new File(name + ".txt");
            Scanner reader = new Scanner(file);
            String data = "";
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                data += line;
            }
            if (data.length() > 0) {
                String[] splitData = data.split(",");
                if (splitData.length != numFeatures){
                    reader.close();
                    writeWeightsToFile();
                    readWeightsFromFile();
                }
                else{
                    for (int i = 0; i < splitData.length; i++){
                        weights[i] = Double.parseDouble(splitData[i]);
                    }
                }
            }
            reader.close();
        } catch(FileNotFoundException e) {
            System.out.println(e);
            writeWeightsToFile();
            readWeightsFromFile();
        }
    }

    public void writeWeightsToFile() {
        try {
            File file = new File(name + ".txt");
            FileWriter writer = new FileWriter(name + ".txt");
            file.createNewFile();
            for (int i = 0; i < weights.length; i++) {
                writer.write(Double.toString(weights[i]));
                if (i < weights.length - 1) {
                    writer.write(",");
                }
            }
            writer.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] arggs){
        new QLearner(Color.blue);
    }
}