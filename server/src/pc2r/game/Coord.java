package pc2r.game;

public class Coord {
    private double x;
    private double y;

    public Coord(double x, double y) {
        this.x = x; this.y = y;
    }

    public Coord() {
        this(0, 0);
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
