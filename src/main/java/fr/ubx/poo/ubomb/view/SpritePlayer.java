/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.view;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.go.character.Player;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpritePlayer extends SpriteCharacter {

    public SpritePlayer(Pane layer, Player player) {
        super(layer, player);
    }

    @Override
    public void updateImage() {
        Player player = (Player) getGameObject();
        Image image = getImage(player.getDirection());
        setImage(image);
    }

    public Image getImage(Direction direction) {
        return ImageResource.getPlayer(direction);
    }
}
