package devtaube.countryside.level.entities;

import devtaube.countryside.level.TileGenerator;
import devtaube.countryside.level.components.ModelComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.ScaleComponent;
import devtaube.countryside.level.components.TilePositionComponent;
import rosequartz.ecb.Entity;

public class Tile extends Entity {

    public static final float TILE_SIZE = 135;

    public Tile(int tileX, int tileZ, int variant) {
        add(
                new PositionComponent(tileX * TILE_SIZE, 0, tileZ * TILE_SIZE),
                new TilePositionComponent(tileX, tileZ),
                new ScaleComponent(TILE_SIZE),
                new ModelComponent(TileGenerator.generateTileArray(), "tiles/tile" + variant + ".png", "tiles/tile" + variant + "b.png")
        );
    }

}
