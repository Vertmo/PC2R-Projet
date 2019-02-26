package pc2r.commands;

import pc2r.game.GameState;
import pc2r.server.Client;

// Parses client commands from string, and creates the appropriate commands
public class CommandParser {
    // The GameState will be passed along the the ClientCommands so they can be executed
    private GameState state;

    public CommandParser(GameState state) {
        this.state = state;
    }

    public ClientCommand parse(String s, Client c) throws CommandParseException {
        System.out.println(s);
        try {
            String[] parts = s.split("/");
            if(parts[0].equals("CONNECT")) {
                return new ConnectCommand(state, c, parts[1]);
            } else if(parts[0].equals("EXIT")) {
                return new ExitCommand(state, c, parts[1]);
            } else if(parts[0].equals("NEWPOS")) {
                String[] coords = parts[1].split("Y");
                float x = Float.parseFloat(coords[0].substring(1, coords[0].length()));
                float y = Float.parseFloat(coords[1]);
                return new NewPosCommand(state, c, x, y);
            } else throw new CommandParseException(s, "name of the command not recognized");
        } catch (Exception e) { throw new CommandParseException(s, e.getMessage()); }
    }
}