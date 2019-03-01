package pc2r.game;

import pc2r.server.Client;
import pc2r.commands.SessionCommand;
import pc2r.commands.TickCommand;
import pc2r.commands.NewObjCommand;
import pc2r.commands.WinnerCommand;

/**
 * Thread that runs the actual game
 */
public class Game extends Thread {
    public static final int server_tickrate = 6;
    public static final int win_cap = 5;

    public static final double ve_radius = 10;
    public static final double objective_radius = 15;

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

            // Wait in the room 10 seconds
            try {
                Thread.sleep(10 * 1000);
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

            int maxScore = 0;
            // Actual gameplay session
            while(maxScore < win_cap) {
                if(state.getNbPlayers() < 1) {
                    System.out.println("All the players have left :(");
                    break;
                }

                // Send position updates
                TickCommand tc = new TickCommand(state.getCoords());
                for(Client c: state.getPlayers().keySet()) {
                    c.send(tc.toString());
                }

                // Detect collisions with objective
                double ox = state.getObjCoord().getX(); double oy = state.getObjCoord().getY();
                for(Player p: state.getPlayers().values()) {
                    double x = p.getCoord().getX(); double y = p.getCoord().getY();
                    if((ox-x)*(ox-x)+(oy-y)*(oy-y) < (ve_radius+objective_radius)*(ve_radius*objective_radius)) {
                        // Update score
                        p.incScore();
                        if(p.getScore() > maxScore) maxScore = p.getScore();

                        // Set new objective
                        state.resetObjective();

                        // Send new objective and scores
                        if(maxScore < win_cap) {
                            NewObjCommand noc = new NewObjCommand(state.getObjCoord(), state.getScores());
                            for(Client c: state.getPlayers().keySet()) {
                                c.send(noc.toString());
                            }
                        }
                        break;
                    }
                }

                try {
                    Thread.sleep(1000/server_tickrate);
                } catch(InterruptedException e) {}
            }

            WinnerCommand wc = new WinnerCommand(state.getScores());
            for(Client c: state.getPlayers().keySet()) {
                c.send(wc.toString());
            }

            // End of a session
            state.stopSession();
        }
    }
}
