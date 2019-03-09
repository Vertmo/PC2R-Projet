package pc2r.commands;

import java.util.Map;

import pc2r.game.Coord;

public class TickCommand extends ServerCommand {
    private Map<String, Coord> coords;
    private Map<String, Coord> speeds;
    private Map<String, Double> angles;

    public TickCommand(Map<String, Coord> coords, Map<String, Coord> speeds, Map<String, Double> angles) {
        this.coords = coords;
        this.speeds = speeds;
        this.angles = angles;
    }

    @Override
    public String toString() {
        String coordsS = "";
        for(String username: coords.keySet()) {
            coordsS += "|" + username + ":" + coords.get(username).toString();
            coordsS += "VX" + speeds.get(username).getX() + "VY" + speeds.get(username).getY();
            coordsS += "A" + angles.get(username);
        }
        return "TICK/" + coordsS.substring(1, coordsS.length()) + "/";
    }
}
