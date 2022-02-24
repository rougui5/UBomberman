package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Player;

import java.util.concurrent.TimeUnit;

public class Bomb extends Decor {
    private long creationTime;
    private long now;

    public Bomb(Game game, Position position, long creation) {
        super(game, position);
        this.creationTime = creation;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    @Override
    public boolean isWalkable(Player player){
        return true;
    }
}
