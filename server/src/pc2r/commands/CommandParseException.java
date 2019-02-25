package pc2r.commands;

public class CommandParseException extends Exception {
    public CommandParseException(String cmd, String msg) {
        super("Couldn't parse " + cmd + " : " + msg);
    }
}
