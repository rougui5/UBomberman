package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class Explosion extends Bonus{
    private long creationTime;
    private long now;

    public Explosion(Position position, long now){
        super(position);
        this.creationTime = now;
    }

    public void updateExplosion(long now){
        setNow(now);
        //the explosions last 0.3 seconds
        if(this.creationTime + 300000000L < this.now){
            this.setDeleted(true);
        }
    }

    public void setNow(long now) {
        this.now = now;
    }

    @Override
    public boolean isWalkable(Player player) {
        return true;
    }

    @Override
    public void takenBy(Player player) {
        player.hitMonsterOrExplosion();
    }
}
