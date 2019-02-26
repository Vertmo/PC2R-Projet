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
        if(state.addPlayer(c, username)) {
            ServerCommand wc = new WelcomeCommand(state.getPhase(),
                                                  state.getScores(),
                                                  state.getPlayer(c).getCoord());
            c.send(wc.toString());

            // Notify the other players of the connection
            ServerCommand npc = new NewPlayerCommand(username);
            for(Client c2: state.getPlayers().keySet()) {
                if(!c2.equals(c)) {
                    c2.send(npc.toString());
                }
            }
        }
        else {
            ServerCommand dc = new DeniedCommand();
            c.send(dc.toString());
        }
    }
}
