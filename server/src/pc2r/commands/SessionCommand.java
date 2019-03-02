package pc2r.commands;

import java.util.Map;
import java.util.List;

import pc2r.game.Coord;

public class SessionCommand extends ServerCommand {
    private Map<String, Coord> coords;
    private Coord objCoord;
    private List<Coord> obsCoords;

    public SessionCommand(Map<String, Coord> coords, Coord objCoord, List<Coord> obsCoords) {
        this.coords = coords;
        this.objCoord = objCoord;
        this.obsCoords = obsCoords;
    }

    @Override
    public String toString() {
        String playerCoords = "";

        for(String username: coords.keySet()) {
            playerCoords += "|" + username + ":" + coords.get(username).toString();
        }

        String obsCoordS = "";
        for(Coord c: obsCoords) {
            obsCoordS += "|" + c.toString();
        }

        return "SESSION/" + playerCoords.substring(1, playerCoords.length()) +
            "/" + objCoord.toString() + "/" + obsCoordS.substring(1, obsCoordS.length()) + "/";
    }
}
