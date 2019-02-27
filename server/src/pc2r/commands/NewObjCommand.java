package pc2r.commands;

import java.util.Map;

import pc2r.game.Coord;

public class NewObjCommand extends ServerCommand {
    private Coord coord;
    private Map<String, Integer> scores;

    public NewObjCommand(Coord coord, Map<String, Integer> scores) {
        this.coord = coord;
        this.scores = scores;
    }

    @Override
    public String toString() {
        String scoreS = "";
        for(String username: scores.keySet()) {
            scoreS += "|" + username + ":" + scores.get(username);
        }

        return "NEWOBJ/" + coord.toString() + "/" + scoreS.substring(1, scoreS.length()) + "/";
    }
}
