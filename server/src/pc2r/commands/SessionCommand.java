package pc2r.commands;

import java.util.Map;
import java.util.List;

import pc2r.game.Coord;

public class SessionCommand extends ServerCommand {
    private Map<String, Coord> coords;
    private List<Coord> objCoords;
    private List<Coord> obsCoords;

    public SessionCommand(Map<String, Coord> coords, List<Coord> objCoords, List<Coord> obsCoords) {
        this.coords = coords;
        this.objCoords = objCoords;
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

        String objCoordS = "";
        for(Coord c: objCoords) {
            objCoordS += "|" + c.toString();
        }

        return "SESSION/" + playerCoords.substring(1, playerCoords.length()) +
            "/" + objCoordS.substring(1, objCoordS.length()) +
            "/" + obsCoordS.substring(1, obsCoordS.length()) + "/";
    }
}
