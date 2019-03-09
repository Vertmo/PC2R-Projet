package pc2r.commands;

public class StunCommand extends ServerCommand {
    private double time;

    public StunCommand(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "STUN/" + time + "/";
    }
}
