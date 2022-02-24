package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.decor.Decor;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class Grid {

    private final int width;
    private final int height;

    private final Map<Position, Decor> elements;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.elements = new Hashtable<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Decor get(Position position) {
        return elements.get(position);
    }

    public void set(Position position, Decor decor) {
        if (decor != null)
            elements.put(position, decor);
    }

    //move a gameobject from one location to another
    public void move(Decor go, Position pos1, Position pos2){
        go.setModified(true);
        go.setPosition(pos2);
        set(pos2, go);
        remove(pos1);
    }

    public void remove(Position position) {
        elements.remove(position);
    }

    public Collection<Decor> values() {
        return elements.values();
    }

}
