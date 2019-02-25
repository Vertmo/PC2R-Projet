package pc2r.commands;

import pc2r.game.GameState;
import pc2r.server.Client;

public class ConnectCommand extends ClientCommand {
    private String username;

    public ConnectCommand(GameState state, Client c, String username) {
        super(state, c);
        this.username = username;
    }

    @Override
    public void execute() {
        // TODO
    }
}
