import java.awt.*;

public class Piece{
    public int x,y,width,row,col;
    public Color color;
    private int tileWidth, totalPadding;
    public Piece(int pwidth, int prow, int pcol, int numRows, int tileWidth, int totalPadding, Color pcolor){
        this.tileWidth = tileWidth;
        this.totalPadding = totalPadding;
        this.width = pwidth;
        this.row = prow;
        this.col = pcol;
        this.color = pcolor;
        setNewLocation();
    }
    private void setNewLocation(){
        this.y = this.row*tileWidth+totalPadding;
        this.x = this.col*tileWidth+totalPadding;
    }
    private void setRows(int row, int col){
        this.row = row;
        this.col = col;
    }
    public void render(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, width, width);
    }
    public void movePieceTo(int row, int col){
        setRows(row,col);
        setNewLocation();
    }
}