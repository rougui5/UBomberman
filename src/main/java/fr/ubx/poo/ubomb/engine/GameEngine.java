/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.engine;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.Bomb;
import fr.ubx.poo.ubomb.go.decor.Box;
import fr.ubx.poo.ubomb.go.decor.Decor;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.view.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private boolean newBomb = false;



    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.stage = stage;
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        Group root = new Group();
        layer = new Pane();

        int height = game.getGrid(currentLevel()).getHeight();
        int width = game.getGrid(currentLevel()).getWidth();
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);

        // Create sprites
        for (Decor decor : game.getGrid(currentLevel()).values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
        }
        sprites.add(new SpritePlayer(layer, player));
    }

    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);
                List<Position> explodingBombs = new LinkedList<>();

                // Do actions
                update(now);
                createNewBombs(now);
                checkCollision(now);
                for(Sprite s : sprites){
                    //when a bomb explodes, it disappears
                    if(s.getGameObject() instanceof Bomb && s.getGameObject().isDeleted()){
                        Position pos = new Position(s.getGameObject().getPosition());
                        explodingBombs.add(pos);
                        player.reclaimBomb();
                    }
                    //monster's random movement
                    if(s.getGameObject() instanceof Monster){
                        Monster monster = (Monster) s.getGameObject();
                        Direction dir = Direction.random();
                        if(monster.canMove(dir)) monster.doMove(dir);
                    }
                }
                for(Position p: explodingBombs){
                    checkExplosions(now, p);
                }

                // Graphic update
                cleanupSprites();

                // management of time for the bombs and explosions
                for(Sprite s : sprites){
                    if (s.getGameObject() instanceof Bomb){
                        Bomb b = (Bomb) s.getGameObject();
                        b.setNow(now);
                        b.setModified(true);
                    }
                    if (s.getGameObject() instanceof Explosion){
                        ((Explosion) s.getGameObject()).updateExplosion(now);
                    }
                };
                //check if the player is going to another level
                if(game.getPlayer().goingToNext){
                    player.goingToNext = false;
                    changeLevel(currentLevel() + 1, true);
                } else if(game.getPlayer().goingToPrev){
                    player.goingToPrev = false;
                    changeLevel(currentLevel() - 1, false);
                }

                render();
                statusBar.update(game);
            }
        };
    }

    //check if creating explosions is possible on each direction
    private void checkExplosions(long now, Position p) {
        checkExplosionSide(now, p, Direction.LEFT);
        checkExplosionSide(now, p, Direction.RIGHT);
        checkExplosionSide(now, p, Direction.UP);
        checkExplosionSide(now, p, Direction.DOWN);
    }

    //check if creating explosions is possible on one specified side
    private void checkExplosionSide(long now, Position p, Direction direction){
            boolean blocked = false;
            int count = 0;
            int range = game.getPlayer().getBombsRange();
            switch (direction) {
                case LEFT:
                    while (count < range && !blocked) {
                        //the explosion is blocked if it hits the boundaries of the level
                        if (!player.checkedBoundaries(p, direction, count)) blocked = true;
                        else{
                            //the explosion can be created if the location is empty, or contains a box or a bonus
                            if (game.getGrid(currentLevel()).get(new Position(p.getX() - (count + 1), p.getY())) == null ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX() - (count + 1), p.getY())) instanceof Box ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX() - (count + 1), p.getY())) instanceof Bonus) {
                                //doors and keys can't explode
                                if (game.getGrid(currentLevel()).get(new Position(p.getX() - (count + 1), p.getY())) instanceof Door
                                        || game.getGrid(currentLevel()).get(new Position(p.getX() - (count + 1), p.getY())) instanceof Key)
                                    blocked = true;
                                else {
                                    //only one box can be destroyed on each side
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX() - (count + 1), p.getY())) instanceof Box)
                                        blocked = true;
                                    //if the princess is killed, the player lose the game
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX() - (count + 1), p.getY())) instanceof Princess){
                                        createExplosion(now, new Position(p.getX() - (count + 1), p.getY()));
                                        player.setLives(0);
                                    }
                                    createExplosion(now, new Position(p.getX() - (count + 1), p.getY()));

                                }
                            } else blocked = true;
                        }
                        count++;
                    }
                case RIGHT:
                    while (count < range && !blocked) {
                        if (!player.checkedBoundaries(p, direction, count)) blocked = true;
                        else {
                            if (game.getGrid(currentLevel()).get(new Position(p.getX() + count + 1, p.getY())) == null ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX() + count + 1, p.getY())) instanceof Box ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX() + count + 1, p.getY())) instanceof Bonus) {
                                if (game.getGrid(currentLevel()).get(new Position(p.getX() + count + 1, p.getY())) instanceof Door
                                        || game.getGrid(currentLevel()).get(new Position(p.getX() + count + 1, p.getY())) instanceof Key)
                                    blocked = true;
                                else {
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX() + count + 1, p.getY())) instanceof Box)
                                        blocked = true;
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX() + count + 1, p.getY())) instanceof Princess){
                                        createExplosion(now, new Position(p.getX() + count + 1, p.getY()));
                                        player.setLives(0);
                                    }
                                    createExplosion(now, new Position(p.getX() + count + 1, p.getY()));
                                }
                            } else blocked = true;
                        }
                        count++;
                    }
                case UP:
                    while (count < range && !blocked) {
                        if (!player.checkedBoundaries(p, direction, count)) blocked = true;
                        else {
                            if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() - (count + 1))) == null ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() - (count + 1))) instanceof Box ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() - (count + 1))) instanceof Bonus) {
                                if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() - (count + 1))) instanceof Door
                                        || game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() - (count + 1))) instanceof Key)
                                    blocked = true;
                                else {
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() - (count + 1))) instanceof Box)
                                        blocked = true;
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() - (count + 1))) instanceof Princess){
                                        createExplosion(now, new Position(p.getX(), p.getY() - (count + 1)));
                                        player.setLives(0);
                                    }
                                    createExplosion(now, new Position(p.getX(), p.getY() - (count + 1)));
                                }
                            } else blocked = true;
                        }
                        count++;
                    }
                case DOWN:
                    while (count < range && !blocked) {
                        if (!player.checkedBoundaries(p, direction, count)) blocked = true;
                        else {
                            if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() + count + 1)) == null ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() + count + 1)) instanceof Box ||
                                    game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() + count + 1)) instanceof Bonus) {
                                if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() + count + 1)) instanceof Door
                                        || game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() + count + 1)) instanceof Key)
                                    blocked = true;
                                else {
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() + count + 1)) instanceof Box)
                                        blocked = true;
                                    if (game.getGrid(currentLevel()).get(new Position(p.getX(), p.getY() + count + 1)) instanceof Princess){
                                        createExplosion(now, new Position(p.getX(), p.getY() + count + 1));
                                        player.setLives(0);
                                    }
                                    createExplosion(now, new Position(p.getX(), p.getY() + count + 1));
                                }
                            } else blocked = true;
                        }
                        count++;
                    }
            }
    }

    //create an explosion at a specified position
    private void createExplosion(long now, Position pos){
        GameObject go = game.getGrid(currentLevel()).get(pos);
        if(go != null) {
            if(go instanceof Box){
                go.remove();
            } else {
                go.explode();
            }
        }
        Explosion exp = new Explosion(pos, now);
        game.getGrid(currentLevel()).set(exp.getPosition(), exp);
        sprites.add(SpriteFactory.create(layer, exp));
        if(player.getPosition().getX() == pos.getX() && player.getPosition().getY() == pos.getY())
            player.hitMonsterOrExplosion();
    }

    //create a bomb at the player's current location
    private void createNewBombs(long now) {
        if(newBomb){
            newBomb = false;
            if(player.getNbBombs() > 0){
                Bomb bb = new Bomb(game, player.getPosition(), now);
                game.getGrid(currentLevel()).set(player.getPosition(), bb);
                sprites.add(new SpriteBomb(layer, bb));
                player.bombUsed();
            }
        }
    }

    private void checkCollision(long now) {
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
        } else if (input.isBomb()){
            newBomb = true;
        } else if (input.isKey()){
            useKey();
        }
        input.clear();
    }

    //method used when the player tries to use a key
    private void useKey(){
            Position playerPos = player.getPosition();
            Position nextPos = player.getDirection().nextPosition(playerPos);
            if(game.getGrid(currentLevel()).get(nextPos) instanceof Door && player.getKeys() > 0){
                Door door = (Door) game.getGrid(currentLevel()).get(nextPos);
                if(!door.isOpened()){
                    door.openDoor();
                    door.setModified(true);
                    player.keyUsed();
                }
            }
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);

        if (player.getLives() == 0) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }

        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }
    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                game.getGrid(currentLevel()).remove(sprite.getPosition());
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    //change the current level when the player reaches an open door
    private void changeLevel(int level, boolean next){
        if(level > game.getHighestLevelReached()){
            game.setHighestLevelReached(level);
            game.monsterVelocity = game.monsterVelocity + 5;
        }
        game.setCurrentLevel(level);
        cleanupSprites();
        sprites.forEach(Sprite::remove);
        sprites.clear();
        if(next) player.setPosition(game.playerInitialPositions.get(level));
        else player.setPosition(game.playerNextPositions.get(level));
        initialize();
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }

    private int currentLevel(){
        return game.getCurrentLevel();
    }
}
