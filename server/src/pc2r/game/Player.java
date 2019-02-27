package pc2r.game;

import java.util.Random;

public class Player {
    private String username;
    private Coord coord;
    private int score;

    public Player(String username) {
        Random r = new Random();
        this.username = username;
        coord = new Coord(r.nextFloat()*2*GameState.w-GameState.w,
                          r.nextFloat()*2*GameState.h-GameState.h);
        score = 0;
    }

    public Coord getCoord() { return coord; }
    public int getScore() { return score; }
    public String getUsername() { return username; }

    public void setCoord(Coord coord) { this.coord = coord; }

    /**
     * Set the player score back to 0
     */
    public void resetScore() { score = 0; }

    /**
     * Increment this player's score
     */
    public void incScore() { score++; }
}
