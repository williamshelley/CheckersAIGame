import java.awt.*;

public class Piece{
    //red or blue
    public Color side;
    private double x,y;
    private int xcol,yrow;
    private double tileWidth, tileHeight;
    private double tilePaddingH, tilePaddingV;
    private double boardPaddingH, boardPaddingV;
    private double pieceWidth, pieceHeight;
    private boolean queen = false;

    public Piece(Color side,
                int xcol, int yrow, 
                double tileWidth, double tileHeight,
                double tilePaddingH, double tilePaddingV,
                double boardPaddingH, double boardPaddingV){
                    
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
        g.setColor(this.side);
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
        System.out.print(Integer.toString(this.xcol) + "," + Integer.toString(this.yrow));
    }

    public boolean isEnemyOf(Piece p){
        if (this.getEnemySide() == p.side){
            return true;
        }
        return false;
    }

    //public int x,y,width,row,col;
    //public String level;
    ////either up or down
    //public String direction;
    //public Color color;
    //public String side;
    //private int tileWidth, totalPadding;
    //private int numRows;
    //public Piece(int pwidth, int prow, int pcol, int pnumRows, int tileWidth, int totalPadding, Color pcolor){
    //    this.tileWidth = tileWidth;
    //    this.totalPadding = totalPadding;
    //    this.width = pwidth;
    //    this.row = prow;
    //    this.col = pcol;
    //    this.color = pcolor;
    //    this.numRows = pnumRows;
    //    this.level = "pawn";
    //    setNewLocation();
    //    setInitialDirection();
    //    setSide();
    //}
    //private void setNewLocation(){
    //    this.y = this.row*tileWidth+totalPadding;
    //    this.x = this.col*tileWidth+totalPadding;
    //}
    //private void setSide(){
    //    if (this.color == Color.red){
    //        this.side = "red";
    //    }
    //    else{
    //        this.side = "blue";
    //    }
    //}
    //private void setRows(int row, int col){
    //    this.row = row;
    //    this.col = col;
    //}
    //private void setInitialDirection(){
    //    if (this.color == Color.red){
    //        this.direction = "down";
    //    }
    //    else{
    //        this.direction = "up";
    //    }
    //}

    //public void movePieceTo(int row, int col){
    //    setRows(row,col);
    //    setNewLocation();
    //}
    //public boolean isNullPiece(){
    //    if (this.row < 0 || this.row >= this.numRows || col < 0 || this.col >= this.numRows){
    //        return true;
    //    }
    //    return false;
    //}
    //public void promote(){
    //    this.level = "queen";
    //    this.direction = "any";
    //    if (this.color == Color.red){
    //        this.color = Color.magenta;
    //    }
    //    if (this.color == Color.blue){
    //        this.color = Color.green;
    //    }
    //}
    //public boolean validDestination(int destrow, int destcol){
    //    return false;
    //}
    //public String enemySide(){
    //    if (this.color == Color.red){
    //        return "blue";
    //    }
    //    else {
    //        return "red";
    //    }
    //}
}