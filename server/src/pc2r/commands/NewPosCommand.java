package pc2r.commands;

import pc2r.game.GameState;
import pc2r.server.Client;

public class NewPosCommand extends ClientCommand {
    private float x, y;

    public NewPosCommand(GameState state, Client c, float x, float y) {
        super(state, c);
        this.x = x; this.y = y;
    }

    @Override
    public void execute() {
        // TODO
    }
}
