package pc2r.commands;

import pc2r.game.GameState;
import pc2r.server.Client;

public class ExitCommand extends ClientCommand {
    private String username;

    public ExitCommand(GameState state, Client c, String username) {
        super(state, c);
        this.username = username;
    }

    @Override
    public void execute() {
        state.removePlayer(c);

        // Notify the other players of the disconnection
        ServerCommand plc = new PlayerLeftCommand(username);
        for(Client c2: state.getPlayers().keySet()) c2.send(plc.toString());
    }
}
