package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.ModelComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.TilePositionComponent;
import rosequartz.ecb.Entity;
import rosequartz.gfx.VertexArray;

public class Grass extends Entity {

    public Grass(VertexArray grassArray, float x, float z, int parentTileX, int parentTileZ) {
        add(
                new PositionComponent(x, 0, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new ModelComponent(grassArray, "models/grass.png", "models/grass_bright.png")
        );
    }

}
