package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.go.decor.Bomb;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteBomb extends SpriteCharacter{
    private int seconds = 0;

    public SpriteBomb(Pane layer, Bomb bomb){
        super(layer, bomb);
    }

    @Override
    //a bomb explodes in 4 seconds, its appearance changes every seconds
    public void updateImage(){
        Bomb bomb = (Bomb) getGameObject();
        if(bomb.getCreationTime() + ((seconds+1) * 1000000000L) < bomb.getNow()){
            seconds++;
        }
        if(seconds <= 3){
            setImage(getImage(3-seconds));
        } else {
            bomb.setDeleted(true);
        }
    }

    private Image getImage(int seconds){
        return ImageResource.getBomb(seconds);
    }
}
