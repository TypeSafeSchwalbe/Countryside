package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.LightComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.TilePositionComponent;
import rosequartz.ecb.Entity;

public class LightSource extends Entity {

    public LightSource(float x, float y, float z, int parentTileX, int parentTileZ, float strength) {
        add(
                new PositionComponent(x, y, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new LightComponent(strength)
        );
    }

}
