public class Point{
    public int x,y;
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }
    public Point(Piece p){
        this.x = p.getxcol();
        this.y = p.getyrow();
    }
    public void print(){
        if (this.x >= 0 && this.y >= 0){
            System.out.print(Integer.toString(this.x) + "," + Integer.toString(this.y));
        }else{
            System.out.print("-,-");
        }
    }
    public boolean equals(Point p){
        return (p.x == this.x && p.y == this.y);
    }
    public boolean isNull(){
        return (this.x < 0 || this.y < 0);
    }
}