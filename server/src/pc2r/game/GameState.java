package pc2r.game;

import java.util.Map;
import java.util.HashMap;
import pc2r.server.Client;

// Store the global state of the game
public class GameState {
    private Map<Client, Player> players;
    private Phase phase;

    public GameState() {
        players = new HashMap<>();
        phase = Phase.Attente;
    }

    public Phase getPhase() { return phase; }

    // Check if there is a player with the same username, and add a new player if not
    // Returns true if the player was added (there was no player with the same name), false otherwise
    public boolean addPlayer(Client c, String username) {
        for(Player p: players.values()) {
            if(p.getUsername().equals(username)) return false;
        }
        players.put(c, new Player(username));
        return true;
    }

    // Remove a player from the game
    public void removePlayer(Client c) {
        players.remove(c);
    }

    public Map<Client, Player> getPlayers() { return players; }

    public Player getPlayer(Client c) { return players.get(c); }

    // Returns the scores for all the players, indexed by their username
    public Map<String, Integer> getScores() {
        Map<String, Integer> scores = new HashMap<>();
        for(Player p: players.values()) {
            scores.put(p.getUsername(), p.getScore());
        }
        return scores;
    }
}
