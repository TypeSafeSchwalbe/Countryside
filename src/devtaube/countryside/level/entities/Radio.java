package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;
import rosequartz.rng.RandomNumberGenerator;

public class Radio extends Entity {

    public Radio(float x, float z, int parentTileX, int parentTileZ) {
        ModelComponent modelComponent = new ModelComponent("models/radio.obj", "models/radio.png", "models/radio_bright.png");
        modelComponent.useAmbientLight = false;
        add(
                new PositionComponent(x, 0, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new ScaleComponent(0.712f),
                new RotationComponent(0, RandomNumberGenerator.getFloat(0, 360), 0),
                new RadioPickupColliderComponent(3),
                modelComponent
        );
    }

}
