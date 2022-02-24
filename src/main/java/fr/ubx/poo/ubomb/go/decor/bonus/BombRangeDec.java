package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class BombRangeDec extends Bonus{
    public BombRangeDec(Position position){
        super(position);
    }

    @Override
    public void takenBy(Player player) {
        player.takeBombRange(false);
        this.setDeleted(true);
    }
}
