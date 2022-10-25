package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;
import rosequartz.rng.RandomNumberGenerator;

public class House extends Entity {

    public House(float x, float z, int parentTileX, int parentTileZ) {
        add(
                new PositionComponent(x, 0, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new RotationComponent(0, RandomNumberGenerator.getFloat(0, 360), 0),
                new ScaleComponent(3),
                new ModelComponent("models/house.obj", "models/house.png", "models/house_bright.png"),
                new SpherePlayerColliderComponent(3)
        );
    }

}
