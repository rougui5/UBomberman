package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;

public class Door extends Bonus{
    private boolean open;
    public boolean next;

    public Door(Position pos, boolean open, boolean next){
        super(pos);
        this.open = open;
        this.next = next;
    }

    public boolean getNext(){ return next; }

    public boolean isOpened(){
        return open;
    }

    public void openDoor(){
        this.open = true;
    }

    @Override
    public void takenBy(Player player) {

        player.takeDoor(this);
    }

    @Override
    public boolean isWalkable(Player player){
        return isOpened();
    }
}
