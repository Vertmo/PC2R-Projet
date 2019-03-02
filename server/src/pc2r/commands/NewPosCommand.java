package pc2r.commands;

import pc2r.game.GameState;
import pc2r.game.Coord;
import pc2r.server.Client;

public class NewPosCommand extends ClientCommand {
    private Coord coord;

    public NewPosCommand(GameState state, Client c, double x, double y) {
        super(state, c);
        coord = new Coord(x, y);
    }

    @Override
    public void execute() {
        // state.getPlayer(c).setCoord(coord);
        System.err.println("Command NEWPOS is no longer accepted since part B of the project");
    }
}
