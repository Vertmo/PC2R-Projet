package pc2r.game;

import java.util.List;
import java.util.ArrayList;

import pc2r.server.Client;
import pc2r.commands.SessionCommand;
import pc2r.commands.TickCommand;
import pc2r.commands.NewObjCommand;
import pc2r.commands.WinnerCommand;
import pc2r.commands.StunCommand;

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
    public static final double bullet_radius = 4;

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
                Thread.sleep(1 * 1000); // TEMP
            } catch(InterruptedException e) {}
            if(state.getNbPlayers() < 1) {
                System.out.println("All the players have left :(");
                continue;
            }

            System.out.println("Session is starting !");
            state.startSession();

            // Send the start session command to the client
            SessionCommand sc = new SessionCommand(state.getCoords(), state.getObjCoords(), state.getObsCoords());
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

                // Update player coords and check for collision, also update stunTime
                for(Player p: state.getPlayers().values()) {
                    p.updateCoord();
                    p.handleCollisions(state.getObsCoords());
                    if(p.isStunned()) p.decreaseStunnedTime(1000/server_refresh_tickrate);
                }

                // Detect collisions with relevant objective for the player
                for(Player p: state.getPlayers().values()) {
                    double ox = state.getObjCoords().get(p.getScore()).getX();
                    double oy = state.getObjCoords().get(p.getScore()).getY();
                    double x = p.getCoord().getX(); double y = p.getCoord().getY();
                    if((ox-x)*(ox-x)+(oy-y)*(oy-y) < (ve_radius+objective_radius)*(ve_radius*objective_radius)) {
                        // Update score
                        p.incScore();
                        if(p.getScore() > maxScore) maxScore = p.getScore();

                        // Set new objective (no longer relevant in extensions)
                        // state.resetObjective();

                        // Send new objective and scores (only the score will be taken into account in the extension)
                        if(maxScore < win_cap) {
                            NewObjCommand noc = new NewObjCommand(state.getObjCoords().get(p.getScore()), state.getScores());
                            for(Client c: state.getPlayers().keySet()) {
                                c.send(noc.toString());
                            }
                        }
                        break;
                    }
                }

                // Extension: jeu de combat

                // Update bullet coords
                for(Bullet b: state.getBullets()) {
                    b.updateCoord();
                }

                // Detect bullets collision with an obstacle
                List<Bullet> newBullets = new ArrayList<>(state.getBullets());
                for(Bullet b: state.getBullets()) {
                    double x = b.getCoord().getX(); double y = b.getCoord().getY();
                    for(Coord obsC: state.getObsCoords()) {
                        double ox = obsC.getX(); double oy = obsC.getY();
                        if((x-ox)*(x-ox)+(y-oy)*(y-oy) < (obs_radius+bullet_radius)*(obs_radius+bullet_radius)) {
                            newBullets.remove(b);
                            break;
                        }
                    }
                }
                state.setBullets(newBullets);

                // Detect bullets collision with a player
                newBullets = new ArrayList<>(state.getBullets());
                for(Bullet b: state.getBullets()) {
                    if(b.isGhost()) b.decreaseGhostTime(1000/server_refresh_tickrate);
                    else {
                        double x = b.getCoord().getX(); double y = b.getCoord().getY();
                        for(Client c: state.getPlayers().keySet()) {
                            Player p = state.getPlayers().get(c);
                            double px = p.getCoord().getX(); double py = p.getCoord().getY();
                            if((x-px)*(x-px)+(y-py)*(y-py) < (ve_radius+bullet_radius)*(ve_radius+bullet_radius)) {
                                p.stun(2000.); // A l'arret pour 2 secondes
                                c.send(new StunCommand(2000.).toString());
                                newBullets.remove(b);
                                break;
                            }
                        }
                    }
                }
                state.setBullets(newBullets);

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
