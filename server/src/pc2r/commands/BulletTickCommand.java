package pc2r.commands;

import java.util.List;
import pc2r.game.Bullet;

public class BulletTickCommand extends ServerCommand {
    private List<Bullet> bullets;

    public BulletTickCommand(List<Bullet> bullets) {
        this.bullets = bullets;
    }

    @Override
    public String toString() {
        if(bullets.isEmpty()) return "BULLETTICK//";
        String bulletS = "";
        for(Bullet b: bullets) {
            bulletS += "|";
            bulletS += "X" + b.getCoord().getX() + "Y" + b.getCoord().getY();
            bulletS += "VX" + b.getSpeed().getX() + "VY" + b.getSpeed().getY();
            bulletS += "A" + b.getAngle();
        }
        return "BULLETTICK/" + bulletS.substring(1, bulletS.length()) + "/";
    }
}
