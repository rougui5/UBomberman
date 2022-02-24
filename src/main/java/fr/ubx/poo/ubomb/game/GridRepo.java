package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.*;


public abstract class GridRepo {

    protected final Game game;

    GridRepo(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public abstract Grid load(int level, String name);

    Decor processEntityCode(EntityCode entityCode, Position pos) {
        switch (entityCode) {
            case Empty:
                return null;
            case Stone:
                return new Stone(pos);
            case Tree:
                return new Tree(pos);
            case Key:
                return new Key(pos);
            case Box:
                return new Box(pos);
            case Heart:
                return new Heart(pos);
            case Monster:
                return new Monster(game, pos);
            case BombRangeDec:
                return new BombRangeDec(pos);
            case Princess:
                return new Princess(pos);
            case BombRangeInc:
                return new BombRangeInc(pos);
            case BombNumberInc:
                return new BombNumberInc(pos);
            case BombNumberDec:
                return new BombNumberDec(pos);
            case DoorNextClosed:
                return new Door(pos, false, true);
            case DoorNextOpened:
                return new Door(pos, true, true);
            case DoorPrevOpened:
                return new Door(pos, true, false);
            default:
                throw new RuntimeException("EntityCode " + entityCode.name() + " not processed");
        }
    }
}
