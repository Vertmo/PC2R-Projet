package pc2r.server;

import java.net.Socket;

import pc2r.game.Game;
import pc2r.game.GameState;
import pc2r.game.Phase;
import pc2r.commands.TickCommand;

public class ClientSender extends Thread {
    private Client client;
    private GameState state;

    public ClientSender(Client client, GameState state) {
        this.client = client;
        this.state = state;
    }

    @Override
    public void run() {
        while(true) {
            if(state.getPhase() == Phase.Jeu) {
                try {
                    TickCommand t = new TickCommand(state.getCoords(), state.getSpeeds(), state.getAngles());
                    client.send(t.toString());
                } catch(Exception e) {
                    // The client has disconnected
                    break;
                }
            }

            try {
                Thread.sleep(1000/Game.server_tickrate);
            } catch(InterruptedException e) {}
        }
    }
}
