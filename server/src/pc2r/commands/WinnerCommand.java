package pc2r.commands;

import java.util.Map;

public class WinnerCommand {
    private Map<String, Integer> scores;

    public WinnerCommand(Map<String, Integer> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        String scoreS = "";
        for(String username: scores.keySet()) {
            scoreS += "|" + username + ":" + scores.get(username);
        }

        return "WINNER/" + scoreS.substring(1, scoreS.length()) + "/";
    }
}
