package pc2r.game;

public class Coord {
    private double x;
    private double y;

    public Coord() {
        x = 0; y = 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    @Override
    public String toString() {
        return "X" + x + "Y" + y;
    }
}
