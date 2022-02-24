package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.bonus.Monster;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteMonster extends Sprite{

    public SpriteMonster(Pane layer, Image image, GameObject monster){ super(layer,image, monster); }

    @Override
    public void updateImage() {
        Monster monster = (Monster) getGameObject();
        Image image = getImage(monster.getDirection());
        setImage(image);
    }

    public Image getImage(Direction direction) {
        return ImageResource.getMonster(direction);
    }
}
