package pc2r.game;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player {
    private String username;
    private Coord coord;
    private int score;

    // Partie B
    private Coord speed;
    private double angle;

    private Lock lock;

    public Player(String username) {
        Random r = new Random();
        this.username = username;
        coord = new Coord(r.nextFloat()*2*GameState.w-GameState.w,
                          r.nextFloat()*2*GameState.h-GameState.h);
        speed = new Coord(); angle = 0;
        score = 0;
        lock = new ReentrantLock();
    }

    public Coord getCoord() { return coord; }
    public int getScore() { return score; }
    public String getUsername() { return username; }
    public Coord getSpeed() { return speed; }
    public double getAngle() { return angle; }
    public Lock getLock() { return lock; }

    public void setCoord(Coord coord) { this.coord = coord; }
    public void setSpeed(Coord speed) { this.speed = speed; }
    public void setAngle(double angle) { this.angle = angle; }

    /**
     * Set the player score back to 0
     */
    public void resetScore() { score = 0; }

    /**
     * Increment this player's score
     */
    public void incScore() { score++; }
}
