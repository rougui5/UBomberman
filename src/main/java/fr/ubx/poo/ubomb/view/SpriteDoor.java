package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.decor.bonus.Door;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteDoor extends Sprite{
    public SpriteDoor(Pane layer, Image image, GameObject gameObject){
        super(layer, image, gameObject);
    }

    @Override
    public void updateImage(){
        Door door = (Door) getGameObject();
        Image image = getImage(door.isOpened());
        setImage(image);
    }

    //doors have two sprites: one when they're open, the other when they're closed
    public Image getImage(boolean open){
        if(open) return ImageResource.DOOR_OPENED.getImage();
        else return ImageResource.DOOR_CLOSED.getImage();
    }
}
