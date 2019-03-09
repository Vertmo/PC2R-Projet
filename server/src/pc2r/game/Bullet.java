package pc2r.game;

import java.util.Map;

import pc2r.server.Client;

public class Bullet extends MovingObject {
    // Pendant un certain temps apres leur tir, les balles sont "fantomes" pour ne pas entrer en collision avec leur tireur
    public double ghostTime;

    public Bullet(Coord coord, Coord speed, double angle) {
        super(coord, speed, angle);
        ghostTime = 250;
    }

    public boolean isGhost() {
        return ghostTime > 0;
    }

    public void decreaseGhostTime(double time) {
        ghostTime -= time;
    }

}
