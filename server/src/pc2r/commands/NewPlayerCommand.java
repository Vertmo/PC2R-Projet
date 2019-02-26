package pc2r.commands;

public class NewPlayerCommand extends ServerCommand {
    private String username;

    public NewPlayerCommand(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "NEWPLAYER/" + username + "/";
    }
}
