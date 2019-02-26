package pc2r.game;

public class Player {
    private String username;
    private Coord coord;
    private int score;

    public Player(String username) {
        this.username = username;
        this.coord = new Coord();
        score = 0;
    }

    public Coord getCoord() { return coord; }
    public int getScore() { return score; }
    public String getUsername() { return username; }

    public void incScore() { this.score++; }
}
