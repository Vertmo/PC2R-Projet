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
        // System.out.println(s);
        try {
            String[] parts = s.split("/");
            if(parts[0].equals("CONNECT")) {
                return new ConnectCommand(state, c, parts[1]);

            } else if(parts[0].equals("EXIT")) {
                return new ExitCommand(state, c, parts[1]);

            } else if(parts[0].equals("NEWPOS")) {
                String[] coords = parts[1].split("Y");
                double x = Double.parseDouble(coords[0].substring(1, coords[0].length()));
                double y = Double.parseDouble(coords[1]);
                return new NewPosCommand(state, c, x, y);

            } else if (parts[0].equals("NEWCOM")) {
                String[] v = parts[1].split("T");
                double a = Double.parseDouble(v[0].substring(1, v[0].length()));
                int nbt = Integer.parseInt(v[1]);
                return new NewComCommand(state, c, a, nbt);

            } else if (parts[0].equals("NEWPOSCOM")) {
                String[] parts1 = parts[1].split("Y");
                String[] parts2 = parts[2].split("T");
                double x = Double.parseDouble(parts1[0].substring(1, parts1[0].length()));
                double y = Double.parseDouble(parts1[1]);
                double a = Double.parseDouble(parts2[0].substring(1, parts2[0].length()));
                int nbt = Integer.parseInt(parts2[1]);
                return new NewPosComCommand(state, c, x, y, a, nbt);

            } else if (parts[0].equals("NEWBULLET")) {
                String[] parts1 = parts[1].split("Y");
                String[] parts2 = parts[2].split("VY");
                double x = Double.parseDouble(parts1[0].substring(1, parts1[0].length()));
                double y = Double.parseDouble(parts1[1]);
                double vx = Double.parseDouble(parts2[0].substring(2, parts2[0].length()));
                double vy = Double.parseDouble(parts2[1]);
                double a = Double.parseDouble(parts[3].substring(1, parts[3].length()));
                return new NewBulletCommand(state, c, x, y, vx, vy, a);

            } else throw new CommandParseException(s, "name of the command not recognized");
        } catch (Exception e) { throw new CommandParseException(s, e.toString()); }
    }
}
