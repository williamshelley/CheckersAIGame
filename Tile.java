import java.awt.*;

public class Tile{
    private Color color;
    private int xcol, yrow;
    private double x,y,width,height;
    private double boardPaddingH,boardPaddingV;

    public Tile(Color color, 
    int xcol, int yrow, 
    double width, double height, 
    double boardPaddingH, double boardPaddingV){
        this.color = color;
        this.xcol = xcol;
        this.yrow = yrow;
        this.width = width;
        this.height = height;
        this.boardPaddingH = boardPaddingH;
        this.boardPaddingV = boardPaddingV;
        this.setX();
        this.setY();
    }

    private void setX(){
        this.x = (double)this.xcol * this.width + this.boardPaddingH;
    }

    private void setY(){
        this.y = (double)this.yrow * this.width + this.boardPaddingV;
    }

    public void render(Graphics g){
        g.setColor(this.color);
        g.fillRect((int)this.x, (int)this.y, (int)this.width, (int)this.height);
    }
}