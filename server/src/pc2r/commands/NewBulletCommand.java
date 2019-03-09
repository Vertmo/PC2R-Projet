package pc2r.commands;

import pc2r.game.GameState;
import pc2r.game.Coord;
import pc2r.game.Bullet;
import pc2r.server.Client;

public class NewBulletCommand extends ClientCommand {
    private Coord coord;
    private Coord speed;
    private double angle;
    public NewBulletCommand(GameState state, Client c,
                            double x, double y, double vx, double vy, double a) {
        super(state, c);
        coord = new Coord(x, y);
        speed = new Coord(vx, vy);
        angle = a;
    }

    @Override
    public void execute() {
        state.getBullets().add(new Bullet(coord, speed, angle));
    }
}
