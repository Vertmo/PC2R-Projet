package pc2r.commands;

import pc2r.game.Game;
import pc2r.game.GameState;
import pc2r.game.Player;
import pc2r.game.Coord;
import pc2r.server.Client;

public class NewComCommand extends ClientCommand {
    private double angle;
    private int nbThrust;

    public NewComCommand(GameState state, Client c, double angle, int nbThrust) {
        super(state, c);
        this.angle = angle; this.nbThrust = nbThrust;
    }

    @Override
    public void execute() {
        Player p = state.getPlayer(c);
        if(!p.isStunned()) {
            p.getLock().lock();
            p.setAngle((p.getAngle()+angle+2*Math.PI) % (2*Math.PI));
            Coord speed = p.getSpeed();
            speed.setX(speed.getX()+(double)nbThrust*Math.cos(p.getAngle()));
            speed.setY(speed.getY()+(double)nbThrust*Math.sin(p.getAngle()));
            p.getLock().unlock();
        }

        // Send a response
        TickCommand t = new TickCommand(state.getCoords(), state.getSpeeds(), state.getAngles());
        c.send(t.toString());
        BulletTickCommand bt = new BulletTickCommand(state.getBullets());
        c.send(bt.toString());
    }
}
