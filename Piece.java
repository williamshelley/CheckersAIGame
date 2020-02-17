import java.awt.*;

public class Piece{
    //red or blue -> pawn colors
    //magenta or green -> queen colors
    private Color side;
    private double x,y;
    private int xcol,yrow;
    private double tileWidth, tileHeight;
    private double tilePaddingH, tilePaddingV;
    private double boardPaddingH, boardPaddingV;
    private double pieceWidth, pieceHeight;
    private boolean queen;
    private Color queenColor;

    public Piece(Piece p){
        this.queen = p.queen;
        this.queenColor = p.queenColor;
        this.side = p.side;
        this.xcol = p.xcol;
        this.yrow = p.yrow;
        this.tileWidth = p.tileWidth;
        this.tileHeight = p.tileHeight;
        this.tilePaddingH = p.tilePaddingH;
        this.tilePaddingV = p.tilePaddingV;
        this.boardPaddingH = p.boardPaddingH;
        this.boardPaddingV = p.boardPaddingV;
        this.pieceWidth = tileWidth - tilePaddingH;
        this.pieceHeight = tileHeight - tilePaddingV;

        this.setX();
        this.setY();
    }

    public Piece(Color side,
                int xcol, int yrow, 
                double tileWidth, double tileHeight,
                double tilePaddingH, double tilePaddingV,
                double boardPaddingH, double boardPaddingV){
        this.queen = false;
        this.side = side;  
        this.xcol = xcol;
        this.yrow = yrow;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tilePaddingH = tilePaddingH;
        this.tilePaddingV = tilePaddingV;
        this.boardPaddingH = boardPaddingH;
        this.boardPaddingV = boardPaddingV;
        this.pieceWidth = tileWidth - tilePaddingH;
        this.pieceHeight = tileHeight - tilePaddingV;
        this.setX();
        this.setY();
        this.setQueenColor();
    }

    private void setX(){
        this.x = (double)this.xcol * this.tileWidth + this.boardPaddingH + this.tilePaddingH / 2.0;
    }

    private void setY(){
        this.y = (double)this.yrow * this.tileHeight + this.boardPaddingV + this.tilePaddingV / 2.0;
    }

    public void move(int xcol, int yrow){
        this.xcol = xcol;
        this.yrow = yrow;
        setX();
        setY();
    }

    public void render(Graphics g){
        if (this.isQueen()){
            g.setColor(this.queenColor);
        }
        else{
            g.setColor(this.side);
        }
        g.fillOval((int)this.x, (int)this.y, (int)this.pieceWidth, (int)this.pieceHeight);
    }

    public Color getEnemySide(){
        if (this.side == Color.blue){
            return Color.red;
        }
        else if (this.side == Color.red){
            return Color.blue;
        }
        else return null;
    }

    public boolean isEnemyOf(Piece p){
        if (this.getEnemyQueenColor() == p.getQueenColor() || this.getEnemySide() == p.getSide()){
            return true;
        }
        return false;
    }

    public boolean isPawn(){
        return !this.queen;
    }

    public void setXY(int xcol, int yrow){
        this.xcol = xcol;
        this.yrow = yrow;
        setX();
        setY();
    }

    private void setQueenColor(){
        if (this.side == Color.blue){
            this.queenColor = Color.green;
        }
        else if (this.side == Color.red){
            this.queenColor = Color.magenta;
        }
    }

    public Color getEnemyQueenColor(){
        if (this.side == Color.blue){
            return Color.magenta;
        }
        else if (this.side == Color.red){
            return Color.green;
        }
        else return null;
    }

    public Color getQueenColor(){
        return this.queenColor;
    }

    public void promote(){
        this.queen = true;
        if (this.side == Color.blue){
            this.queenColor = Color.green;
        }
        else if (this.side == Color.red){
            this.queenColor = Color.magenta;
        }
    }

    public boolean isQueen(){
        return queen;
    }

    public int getxcol(){
        return xcol;
    }

    public int getyrow(){
        return yrow;
    }

    public Color getSide(){
        return this.side;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getWidth(){
        return this.pieceWidth;
    }

    public double getHeight(){
        return this.pieceHeight;
    }

    public boolean isNull(){
        if (this.xcol < 0 || this.yrow < 0 || this.pieceWidth <= 0 || this.pieceHeight <= 0){
            return true;
        }
        return false;
    }

    public void print(){
        System.out.print(this.toString());
    }

    public Color queenSide(){
        if (this.queenColor == Color.green){
            return Color.blue;
        }
        else if(this.queenColor == Color.magenta){
            return Color.red;
        }
        return null;
    }

    public boolean isAllyOf(Piece p){
        return (this.side == p.side || this.queenSide() == p.queenSide());
    }

    public String toString(){
        String res = Integer.toString(this.xcol) + "," + Integer.toString(this.yrow);
        if (this.isNull()){
            res = "-,-";
        }
        return res;
    }
}