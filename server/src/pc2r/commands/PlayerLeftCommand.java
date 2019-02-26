package pc2r.commands;

public class PlayerLeftCommand extends ServerCommand {
    private String username;

    public PlayerLeftCommand(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "PLAYERLEFT/" + username + "/";
    }
}
