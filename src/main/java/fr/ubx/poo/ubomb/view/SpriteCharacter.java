package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Player;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteCharacter extends Sprite{
    protected final ColorAdjust effect = new ColorAdjust();

    public SpriteCharacter(Pane layer, GameObject character){
        super(layer, null, character);
        effect.setBrightness(0.8);
        updateImage();
    }

    @Override
    public void updateImage() {
    }

    public Image getImage(Direction direction) {
        return null;
    }
}
