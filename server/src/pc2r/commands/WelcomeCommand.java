package pc2r.commands;

import java.util.Map;

import pc2r.game.Phase;
import pc2r.game.Coord;

public class WelcomeCommand extends ServerCommand {
    private Phase phase;
    private Map<String, Integer> scores;
    private Coord coord;

    public WelcomeCommand(Phase phase, Map<String, Integer> scores, Coord coord) {
        this.phase = phase;
        this.scores = scores;
        this.coord = coord;
    }

    @Override
    public String toString() {
        String ph;
        if(phase == Phase.Attente) ph = "attente";
        else ph = "jeu";

        String score = "";
        for(String username: scores.keySet()) {
            score += "|" + username + ":" + scores.get(username);
        }

        return "WELCOME/" + ph + "/" + score.substring(1, score.length()) + "/" + coord +  "/";
    }
}
