package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.ModelComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.SpherePlayerColliderComponent;
import devtaube.countryside.level.components.TilePositionComponent;
import rosequartz.ecb.Entity;

public class Lantern extends Entity {

    public Lantern(float x, float z, int parentTileX, int parentTileZ) {
        ModelComponent modelComponent = new ModelComponent("models/lantern.obj", "models/lantern.png", "models/lantern_bright.png");
        modelComponent.useAmbientLight = false;
        add(
                new PositionComponent(x, 0, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new SpherePlayerColliderComponent(0.2f),
                modelComponent
        );
    }

}
