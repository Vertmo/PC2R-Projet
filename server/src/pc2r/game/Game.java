package pc2r.game;

import pc2r.server.Client;
import pc2r.commands.SessionCommand;

/**
 * Thread that runs the actual game
 */
public class Game extends Thread {
    public static final int server_tickrate = 6;

    private GameState state;

    public Game(GameState state) {
        this.state = state;
    }

    @Override
    public void run() {
        while(true) {
            // Wait until there is at least 1 player
            state.waitUntilHasPlayers();
            System.out.println("Has players !");

            // Wait in the room 20 seconds
            try {
                Thread.sleep(/*20 TEMP */0 * 1000);
            } catch(InterruptedException e) {}
            if(state.getNbPlayers() < 1) {
                System.out.println("All the players have left :(");
                continue;
            }

            System.out.println("Session is starting !");
            state.startSession();

            // Send the start session command to the client
            SessionCommand sc = new SessionCommand(state.getCoords(), state.getObjCoord());
            for(Client c: state.getPlayers().keySet()) {
                c.send(sc.toString());
            }

            // Actual gameplay session
            while(true) {
                if(state.getNbPlayers() < 1) {
                    System.out.println("All the players have left :(");
                    break;
                }

                // TODO

                try {
                    Thread.sleep(1000/server_tickrate);
                } catch(InterruptedException e) {}
            }
        }
    }
}
