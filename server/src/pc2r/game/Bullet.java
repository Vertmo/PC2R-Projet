package pc2r.game;

import java.util.List;

public class Bullet extends MovingObject {
    public Bullet(Coord coord, Coord speed, double angle) {
        super(coord, speed, angle);
    }

    @Override
    public void handleCollisions(List<Coord> obstacleCoords) {
        // TODO
    }
}
