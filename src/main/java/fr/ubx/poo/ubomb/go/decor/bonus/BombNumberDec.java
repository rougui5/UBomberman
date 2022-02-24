package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.Takeable;
import fr.ubx.poo.ubomb.go.character.Player;

public class BombNumberDec extends Bonus implements Takeable {

    public BombNumberDec(Position position){
        super(position);
    }

    @Override
    public void takenBy(Player player) {
        player.takeBombNumber(false);
        this.setDeleted(true);
    }
}
