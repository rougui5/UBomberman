/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.decor.Box;
import fr.ubx.poo.ubomb.go.decor.bonus.*;


public class Player extends GameObject implements Movable {

    private Direction direction;
    private boolean moveRequested = false;
    private int lives;
    private int keys = 0;
    private int nbBombs = 3;
    private int bombsRange = 1;
    public boolean goingToNext = false;
    public boolean goingToPrev = false;
    public boolean invincibleStart = false;
    public boolean stillInvincible = false;
    private long invincibilityStart;



    public int getKeys() {
        return keys;
    }
    public void keyUsed() { this.keys--; }

    public int getNbBombs() {
        return nbBombs;
    }

    public int getBombsRange() {
        return bombsRange;
    }

    private boolean winner = false;

    public Player(Game game, Position position, int lives) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = lives;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives){ this.lives = lives; }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    //return if the player is not blocked by any boundary or obstacle
    public final boolean canMove(Direction direction) {
        switch(direction){
            case UP:
                if(this.getPosition().getY() == 0){
                    return false;
                } else {
                    return notBlockedByObstacle(direction);
                }
            case DOWN:
                if(this.getPosition().getY() == this.game.getGrid(currentLevel()).getHeight() - 1) return false;
                else {
                    return notBlockedByObstacle(direction);
                }
            case LEFT:
                if(this.getPosition().getX() == 0) return false;
                else {
                    return notBlockedByObstacle(direction);
                }
            case RIGHT:
                if(this.getPosition().getX() == this.game.getGrid(currentLevel()).getWidth() - 1) return false;
                else {
                    return notBlockedByObstacle(direction);
                }
            default:
                return true;
        }
    }

    //check that an element is not blocked by the boundaries of the level
    public boolean checkedBoundaries(Position pos, Direction dir, int range){
        switch(dir){
            case LEFT:
                if(pos.getX() == 0 + range) return false;
                else return true;
            case RIGHT:
                if(pos.getX() == this.game.getGrid(currentLevel()).getWidth() - (1 + range)) return false;
                else return true;
            case UP:
                if(pos.getY() == 0 + range) return false;
                else return true;
            case DOWN:
                if(pos.getY() == this.game.getGrid(currentLevel()).getHeight() - (1 + range)) return false;
                else return true;
            default:
                return true;
        }
    }

    //check that an element is not blocked by ANYTHING
    public boolean notBlockedByObstacle(Direction direction){
        Position next = direction.nextPosition(this.getPosition());
        if(this.game.getGrid(currentLevel()).get(next) == null) return true;
        else if(this.game.getGrid(currentLevel()).get(next).isWalkable(this)) return true;
        else if(this.game.getGrid(currentLevel()).get(next) instanceof Box && this.game.getGrid(currentLevel()).get(direction.nextPosition(next)) == null
        && checkedBoundaries(next, direction, 0)) return true;
        else return false;
    }

    public void update(long now) {
        //the player just became invincible, its invincibility time starts
        if(invincibleStart){
            invincibleStart = false;
            invincibilityStart = now;
            setModified(true);
        }
        //the invincibility lasts one second
        if(stillInvincible && invincibilityStart + 1000000000L < now){
            stillInvincible = false;
            setModified(true);
        }
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }

    public void doMove(Direction direction) {
        // Check if we need to pick something up
        Position nextPos = direction.nextPosition(getPosition());
        if(this.game.getGrid(currentLevel()).get(nextPos) instanceof Monster){
            Monster monster = (Monster) this.game.getGrid(currentLevel()).get(nextPos);
            monster.takenBy(this);
        } else if(this.game.getGrid(currentLevel()).get(nextPos) instanceof Princess){
            Princess princess = (Princess) this.game.getGrid(currentLevel()).get(nextPos);
            princess.takenBy(this);
        } else if(this.game.getGrid(currentLevel()).get(nextPos) instanceof Heart){
            Heart heart = (Heart) this.game.getGrid(currentLevel()).get(nextPos);
            heart.takenBy(this);
        } else if(this.game.getGrid(currentLevel()).get(nextPos) instanceof BombNumberInc){
            ((BombNumberInc) this.game.getGrid(currentLevel()).get(nextPos)).takenBy(this);
        } else if (this.game.getGrid(currentLevel()).get(nextPos) instanceof BombRangeInc) {
            ((BombRangeInc) this.game.getGrid(currentLevel()).get(nextPos)).takenBy(this);
        } else if (this.game.getGrid(currentLevel()).get(nextPos) instanceof BombRangeDec) {
            ((BombRangeDec) this.game.getGrid(currentLevel()).get(nextPos)).takenBy(this);
        } else if (this.game.getGrid(currentLevel()).get(nextPos) instanceof BombNumberDec) {
            ((BombNumberDec) this.game.getGrid(currentLevel()).get(nextPos)).takenBy(this);
        } else if (this.game.getGrid(currentLevel()).get(nextPos) instanceof Key){
            ((Key) this.game.getGrid(currentLevel()).get(nextPos)).takenBy(this);
        } else if (this.game.getGrid(currentLevel()).get(nextPos) instanceof Explosion){
            ((Explosion) this.game.getGrid(currentLevel()).get(nextPos)).takenBy(this);
        } else if (this.game.getGrid(currentLevel()).get(nextPos) instanceof Box){
            Box box = (Box) this.game.getGrid(currentLevel()).get(nextPos);
            this.game.getGrid(currentLevel()).move(box, nextPos, direction.nextPosition(nextPos));
        } else if (this.game.getGrid(currentLevel()).get(nextPos) instanceof Door){
            ((Door) this.game.getGrid(currentLevel()).get(nextPos)).takenBy(this);
        }
        setPosition(nextPos);
    }

    @Override
    public boolean isWalkable(Player player) {
        return false;
    }

    @Override
    public void explode() {
    }

    public void takeKey() {
        this.keys++;
    }

    public void takeHeart(){
        this.lives++;
    }

    public void takeBombNumber(boolean type){
        if(type){
            this.nbBombs++;
            this.game.bombBagCapacity++;
        } else {
            if(this.game.bombBagCapacity > 1){
                this.nbBombs--;
                this.game.bombBagCapacity--;
            }
        }
    }

    public void takeBombRange(boolean type){
        if(type){
            this.bombsRange++;
        } else {
            if(this.bombsRange > 1){
                this.bombsRange--;
            }
        }
    }

    //if the player is hit, he becomes invincible
    public void hitMonsterOrExplosion(){
        if(!stillInvincible) {
            this.lives--;
            invincibleStart = true;
            stillInvincible = true;
        }
    }

    public void reachPrincess(){
        this.winner = true;
    }

    public void bombUsed(){
        if(this.nbBombs >= 0) this.nbBombs--;
    }

    public boolean isWinner() {
        return this.winner;
    }

    public void reclaimBomb(){
        this.nbBombs++;
    }

    private int currentLevel(){
        return game.getCurrentLevel();
    }

    //determines if the player is going to a previous level or a new one
    public void takeDoor(Door door){
        if(door.getNext()){
            this.goingToNext = true;
        } else this.goingToPrev = true;
    }
}
