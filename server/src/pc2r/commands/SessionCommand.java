package pc2r.commands;

import java.util.Map;
import pc2r.game.Coord;

public class SessionCommand extends ServerCommand {
    private Map<String, Coord> coords;
    private Coord objCoord;

    public SessionCommand(Map<String, Coord> coords, Coord objCoord) {
        this.coords = coords;
        this.objCoord = objCoord;
    }

    @Override
    public String toString() {
        String playerCoords = "";

        for(String username: coords.keySet()) {
            playerCoords += "|" + username + ":" + coords.get(username).toString();
        }

        return "SESSION/" + playerCoords.substring(1, playerCoords.length()) + "/" + objCoord.toString() + "/";
    }
}
