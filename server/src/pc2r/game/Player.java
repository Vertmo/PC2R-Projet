package pc2r.game;

import java.util.Random;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player extends MovingObject {
    private String username;
    private int score;
    private Lock lock;

    public Player(String username) {
        super(new Coord(0, 0), new Coord(0, 0), 0);

        Random r = new Random();
        coord = new Coord(r.nextFloat()*2*Game.w-Game.w,
                          r.nextFloat()*2*Game.h-Game.h);
        speed = new Coord();
        this.username = username;
        score = 0;
        lock = new ReentrantLock();
    }

    public int getScore() { return score; }
    public String getUsername() { return username; }
    public Lock getLock() { return lock; }

    /**
     * Set the player score back to 0
     */
    public void resetScore() { score = 0; }

    /**
     * Increment this player's score
     */
    public void incScore() { score++; }

    @Override
    public synchronized void handleCollisions(List<Coord> obstacleCoords) {
        double x = coord.getX(); double y = coord.getY();
        for(Coord obsC: obstacleCoords) {
            double ox = obsC.getX(); double oy = obsC.getY();
            if((x-ox)*(x-ox)+(y-oy)*(y-oy) < (Game.ve_radius+Game.obs_radius)*(Game.ve_radius+Game.obs_radius)) {
                // Only go away from the obstacle (avoid getting stuck inside)
                if((x-ox)*speed.getX() < 0) speed.setX(-speed.getX());
                if((y-oy)*speed.getY() < 0) speed.setY(-speed.getY());
                break;
            }
        }
    }
}
