package pc2r.game;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import pc2r.server.Client;

/**
 * Store the global state of the game
 */
public class GameState {
    private Map<Client, Player> players;
    private Phase phase;
    private List<Coord> objCoords;
    private List<Coord> obsCoords;

    private Lock lHasPlayers; private Condition hasPlayers;

    public GameState() {
        players = new HashMap<>();
        phase = Phase.Attente;
        lHasPlayers = new ReentrantLock(); hasPlayers = lHasPlayers.newCondition();
        objCoords = new ArrayList<>();
        obsCoords = new ArrayList<>();
    }

    public Phase getPhase() { return phase; }
    public Map<Client, Player> getPlayers() { return players; }
    public Player getPlayer(Client c) { return players.get(c); }
    public int getNbPlayers() { return players.size(); }
    public List<Coord> getObjCoords() { return objCoords; }
    public List<Coord> getObsCoords() { return obsCoords; }

    /**
     * Check if there is a player with the same username, and add a new player if not
     * @returns true if the player was added (there was no player with the same name), false otherwise
     */
    public synchronized boolean addPlayer(Client c, String username) {
        for(Player p: players.values()) {
            if(p.getUsername().equals(username)) return false;
        }
        players.put(c, new Player(username));

        lHasPlayers.lock();
        hasPlayers.signal();
        lHasPlayers.unlock();

        return true;
    }

    /**
     * Remove a player from the game
     */
    public synchronized void removePlayer(Client c) {
        players.remove(c);
    }

    /**
     * @returns the scores for all the players, indexed by their username
     */
    public Map<String, Integer> getScores() {
        Map<String, Integer> scores = new HashMap<>();
        for(Player p: players.values()) {
            scores.put(p.getUsername(), p.getScore());
        }
        return scores;
    }

    /**
     * @returns the coordinates for all the players, indexed by their username
     */
    public Map<String, Coord> getCoords() {
        Map<String, Coord> coords = new HashMap<>();
        for(Player p: players.values()) {
            coords.put(p.getUsername(), p.getCoord());
        }
        return coords;
    }

    /**
     * @returns the speeds for all the players, indexed by their username
     */
    public Map<String, Coord> getSpeeds() {
        Map<String, Coord> speeds = new HashMap<>();
        for(Player p: players.values()) {
            speeds.put(p.getUsername(), p.getSpeed());
        }
        return speeds;
    }

    /**
     * @returns the angles for all the players, indexed by their username
     */
    public Map<String, Double> getAngles() {
        Map<String, Double> angles = new HashMap<>();
        for(Player p: players.values()) {
            angles.put(p.getUsername(), p.getAngle());
        }
        return angles;
    }

    /**
     * Wait until the game has players
     */
    public void waitUntilHasPlayers() {
        lHasPlayers.lock();
        try {
            while(getNbPlayers() < 1) hasPlayers.await();
        } catch (InterruptedException e) {}
        lHasPlayers.unlock();
    }

    /**
     * Set phase to Jeu, set a new objective, and generate random coords for the players
     */
    public synchronized void startSession() {
        Random r = new Random();
        phase = Phase.Jeu;

        // Generate ordered objective coords
        objCoords.clear();
        for(int i = 0; i < Game.win_cap; i++) {
            objCoords.add(new Coord(r.nextDouble()*2*Game.w-Game.w, r.nextDouble()*2*Game.h-Game.h));
        }

        // Generate obstacles (between 3 and 6)
        obsCoords.clear();
        int nbObs = r.nextInt(3) + 3;
        for(int i = 0; i < nbObs; i++) {
            obsCoords.add(new Coord(r.nextDouble()*2*Game.w-Game.w, r.nextDouble()*2*Game.h-Game.h));
        }

        for(Player p: players.values()) {
            p.getCoord().setX(r.nextDouble()*2*Game.w-Game.w); p.getCoord().setY(r.nextDouble()*2*Game.h-Game.h);
            p.setSpeed(new Coord());
            p.resetScore();
        }
    }

    /**
     * Set phase to Attente
     */
    public synchronized void stopSession() {
        phase = Phase.Attente;
    }

    /**
     * Set a new objective
     * No longer relevant in extensions
     */
    // public synchronized void resetObjective() {
    //     Random r = new Random();
    //     objCoord = new Coord(r.nextDouble()*2*Game.w-Game.w, r.nextDouble()*2*Game.h-Game.h);
    // }
}
