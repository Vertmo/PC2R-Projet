package pc2r.game;

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
    public static final int w = 350;
    public static final int h = 300;

    private Map<Client, Player> players;
    private Phase phase;
    private Coord objCoord;

    private Lock lHasPlayers; private Condition hasPlayers;

    public GameState() {
        players = new HashMap<>();
        phase = Phase.Attente;
        lHasPlayers = new ReentrantLock(); hasPlayers = lHasPlayers.newCondition();
        objCoord = new Coord();
    }

    public Phase getPhase() { return phase; }

    public Map<Client, Player> getPlayers() { return players; }

    public Player getPlayer(Client c) { return players.get(c); }

    public int getNbPlayers() { return players.size(); }

    public Coord getObjCoord() { return objCoord; }

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
    public void startSession() {
        Random r = new Random();
        phase = Phase.Jeu;
        objCoord.setX(r.nextFloat()*2*w-w); objCoord.setY(r.nextFloat()*2*h-h);

        for(Player p: players.values()) {
            p.getCoord().setX(r.nextFloat()*2*w-w); p.getCoord().setY(r.nextFloat()*2*h-h);
        }
    }

    /**
     * Set phase to Attente
     */
    public void stopSession() { phase = Phase.Attente; }
}
