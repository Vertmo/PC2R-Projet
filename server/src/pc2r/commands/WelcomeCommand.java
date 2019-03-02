package pc2r.commands;

import java.util.List;
import java.util.Map;

import pc2r.game.Phase;
import pc2r.game.Coord;

public class WelcomeCommand extends ServerCommand {
    private Phase phase;
    private Map<String, Integer> scores;
    private Coord coord;
    private List<Coord> obsCoords;

    public WelcomeCommand(Phase phase, Map<String, Integer> scores, Coord coord, List<Coord> obsCoords) {
        this.phase = phase;
        this.scores = scores;
        this.coord = coord;
        this.obsCoords = obsCoords;
    }

    @Override
    public String toString() {
        String ph;
        if(phase == Phase.Attente) ph = "attente";
        else ph = "jeu";

        String scoreS = "";
        for(String username: scores.keySet()) {
            scoreS += "|" + username + ":" + scores.get(username);
        }

        if(obsCoords.isEmpty()) {
            return "WELCOME/" + ph + "/" + scoreS.substring(1, scoreS.length()) + "/" + coord + "/";
        }

        String obsCoordS = "";
        for(Coord c: obsCoords) {
            obsCoordS += "|" + c.toString();
        }

        return "WELCOME/" + ph + "/" + scoreS.substring(1, scoreS.length()) +
            "/" + coord +  "/" + obsCoordS.substring(1, obsCoordS.length()) + "/";
    }
}
