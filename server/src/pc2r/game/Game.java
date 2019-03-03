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
    public static final int server_refresh_tickrate = 60;
    public static final int server_tickrate = 6;
    public static final int win_cap = 5;

    public static final double thrustit = 0.2;

    public static final int w = 350;
    public static final int h = 300;
    public static final double ve_radius = 10;
    public static final double objective_radius = 10;
    public static final double obs_radius = 10;

    private GameState state;

    public Game(GameState state) {
        this.state = state;
    }

    @Override
    public void run() {
        while(true) {
            // Wait until there is at least 1 player
            state.waitUntilHasPlayers();
            System.out.println("Session has players !");

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
            SessionCommand sc = new SessionCommand(state.getCoords(), state.getObjCoord(), state.getObsCoords());
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

                // Send position updates (only in Partie A)
                // TickCommand tc = new TickCommand(state.getCoords());
                // for(Client c: state.getPlayers().keySet()) {
                //     c.send(tc.toString());
                // }

                // Update coordinates according to speed and angle
                for(Player p: state.getPlayers().values()) {
                    p.getLock().lock();
                    Coord coord = p.getCoord();
                    Coord speed = p.getSpeed();
                    coord.setX((coord.getX()+speed.getX()+3*Game.w)%(2*Game.w)-Game.w);
                    coord.setY((coord.getY()+speed.getY()+3*Game.h)%(2*Game.h)-Game.h);

                    // Check if we've collided with an obstacle
                    double x = coord.getX(); double y = coord.getY();
                    for(Coord obsC: state.getObsCoords()) {
                        double ox = obsC.getX(); double oy = obsC.getY();
                        if((x-ox)*(x-ox)+(y-oy)*(y-oy) < (ve_radius+obs_radius)*(ve_radius+obs_radius)) {
                            // Only go away from the obstacle (avoid getting stuck inside)
                            if((x-ox)*speed.getX() < 0) speed.setX(-speed.getX());
                            if((y-oy)*speed.getY() < 0)speed.setY(-speed.getY());
                            break;
                        }
                    }
                    p.getLock().unlock();
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
                    Thread.sleep(1000/server_refresh_tickrate);
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
