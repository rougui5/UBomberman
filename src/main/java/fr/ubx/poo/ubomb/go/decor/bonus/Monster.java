package fr.ubx.poo.ubomb.go.decor.bonus;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.Box;

import java.util.Random;

public class Monster extends Bonus implements Movable {
    private Direction direction;
    public Monster(Game game, Position position){
        super(position);
        this.game = game;
        this.direction = Direction.DOWN;
    }

    @Override
    public boolean isWalkable(Player player){
        return true;
    }

    public Direction getDirection(){ return this.direction; }

    public void setDirection(Direction direction){ this.direction = direction; }

    @Override
    public void takenBy(Player player) {
        player.hitMonsterOrExplosion();
    }

    @Override
    public boolean canMove(Direction direction) {
        Random rand = new Random();
        int number = rand.nextInt(600);
        if(number < game.monsterVelocity){
            switch(direction){
                case UP:
                    if(this.getPosition().getY() == 0) return false;
                    else return notBlockedByObstacle(direction);
                case DOWN:
                    if(this.getPosition().getY() == this.game.getGrid(currentLevel()).getHeight() - 1) return false;
                    else return notBlockedByObstacle(direction);
                case LEFT:
                    if(this.getPosition().getX() == 0) return false;
                    else return notBlockedByObstacle(direction);
                case RIGHT:
                    if(this.getPosition().getX() == this.game.getGrid(currentLevel()).getWidth() - 1) return false;
                    else return notBlockedByObstacle(direction);
                default:
                    return true;
            }
        }
        else return false;
    }

    public boolean notBlockedByObstacle(Direction direction){
        Position next = direction.nextPosition(this.getPosition());
        if(this.game.getGrid(currentLevel()).get(next) == null) return true;
        else return false;
    }

    private int currentLevel(){
        return game.getCurrentLevel();
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(this.getPosition());
        setDirection(direction);
        game.getGrid(currentLevel()).move(this, getPosition(), nextPos);
        Position playerPos = game.getPlayer().getPosition();
        if(nextPos.getX() == playerPos.getX() && nextPos.getY() == playerPos.getY()) game.getPlayer().hitMonsterOrExplosion();
    }
}
