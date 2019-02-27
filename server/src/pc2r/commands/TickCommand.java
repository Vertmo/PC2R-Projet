package pc2r.commands;

import java.util.Map;

import pc2r.game.Coord;

public class TickCommand extends ServerCommand {
    private Map<String, Coord> coords;

    public TickCommand(Map<String, Coord> coords) {
        this.coords = coords;
    }

    @Override
    public String toString() {
        String coordsS = "";
        for(String username: coords.keySet()) {
            coordsS += "|" + username + ":" + coords.get(username).toString();
        }
        return "TICK/" + coordsS.substring(1, coordsS.length()) + "/";
    }
}
