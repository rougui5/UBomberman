/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.game;


import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Game {

    public int bombBagCapacity;
    public int monsterVelocity;
    public final int playerLives;
    public final int levels;
    private int currentLevel = 1;
    private int highestLevelReached = 1;
    public final long playerInvisibilityTime;
    public final long monsterInvisibilityTime;
    private HashMap<Integer, Grid> grids;
    private final Player player;
    public final String samplePath;
    public HashMap <Integer, Position> playerInitialPositions = new HashMap<>();
    public HashMap <Integer, Position> playerNextPositions = new HashMap<>();

    public Game(String worldPath) {
        samplePath = worldPath;
        grids = new HashMap<>();
        try (InputStream input = new FileInputStream(new File(worldPath, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            bombBagCapacity = Integer.parseInt(prop.getProperty("bombBagCapacity", "3"));
            monsterVelocity = Integer.parseInt(prop.getProperty("monsterVelocity", "10"));
            levels = Integer.parseInt(prop.getProperty("levels", "1"));
            playerLives = Integer.parseInt(prop.getProperty("playerLives", "3"));
            playerInvisibilityTime = Long.parseLong(prop.getProperty("playerInvisibilityTime", "4000"));
            monsterInvisibilityTime = Long.parseLong(prop.getProperty("monsterInvisibilityTime", "1000"));

            // Load the world
            String prefix = prop.getProperty("prefix");
            GridRepo gridRepo = new GridRepoFile(this);
            for(int i = 1; i <= levels; i++){
                grids.put(i, gridRepo.load(i, prefix + i + ".txt"));
            }

            // Create the player
            String[] tokens = prop.getProperty("player").split("[ :x]+");
            if (tokens.length != 2)
                throw new RuntimeException("Invalid configuration format");
            playerInitialPositions.put(1, new Position(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));
            player = new Player(this, playerInitialPositions.get(1), playerLives);

        } catch (IOException ex) {
            System.err.println("Error loading configuration");
            throw new RuntimeException("Invalid configuration format");
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Grid getGrid(int level) {
        return grids.get(level);
    }

    // Returns the player, monsters and bombs at a given position
    public List<GameObject> getGameObjects(Position position) {
        List<GameObject> gos = new LinkedList<>();
        if (getPlayer().getPosition().equals(position))
            gos.add(player);
        return gos;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean inside(Position position) {
        return true;
    }

    public int getHighestLevelReached() {
        return highestLevelReached;
    }

    public void setHighestLevelReached(int highestLevelReached) {
        this.highestLevelReached = highestLevelReached;
    }
}
