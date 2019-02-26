package pc2r.commands;

import pc2r.game.GameState;
import pc2r.server.Client;

// Command sent from the Client to the server
public abstract class ClientCommand implements Command {
    // State on which commands will be executed
    protected GameState state;

    // Client that sent the command
    protected Client c;

    public ClientCommand(GameState state, Client c) {
        this.state = state;
        this.c = c;
    }

    public abstract void execute();
}
