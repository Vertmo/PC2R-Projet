package pc2r.game;

import java.util.List;

public abstract class MovingObject {
    protected Coord coord;
    protected Coord speed;
    protected double angle;

    public MovingObject(Coord coord, Coord speed, double angle) {
        this.coord = coord; this.speed = speed; this.angle = angle;
    }

    public Coord getCoord() { return coord; }
    public Coord getSpeed() { return speed; }
    public double getAngle() { return angle; }

    public void setCoord(Coord coord) { this.coord = coord; }
    public void setSpeed(Coord speed) { this.speed = speed; }
    public void setAngle(double angle) { this.angle = angle; }

    /**
     * Update this object coordinates according to it's speed
     */
    public synchronized void updateCoord() {
        coord.setX((coord.getX()+speed.getX()+3*Game.w)%(2*Game.w)-Game.w);
        coord.setY((coord.getY()+speed.getY()+3*Game.h)%(2*Game.h)-Game.h);
    }
}
