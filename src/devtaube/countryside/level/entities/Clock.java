package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;
import rosequartz.gfx.VertexArray;
import rosequartz.rng.RandomNumberGenerator;

public class Clock extends Entity {

    private static VertexArray generateVertexArray() {
        return new VertexArray(3, 3, 2)
                .vertex(  0.2225f, 0, -0.356f,   0, 1, 0,   0, 0 )
                .vertex( -0.2225f, 0, -0.356f,   0, 1, 0,   1, 0 )
                .vertex(  0.2225f, 0,  0.356f,   0, 1, 0,   0, 1 )
                .vertex( -0.2225f, 0,  0.356f,   0, 1, 0,   1, 1 )
                .fragment( 0, 1, 2 )
                .fragment( 1, 3, 2 )
                .upload();
    }

    public Clock(float x, float z, int parentTileX, int parentTileZ) {
        ModelComponent modelComponent = new ModelComponent(generateVertexArray(), "models/clock.png", "models/clock_bright.png");
        modelComponent.useAmbientLight = false;
        add(
                new PositionComponent(x, 0.01f, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new ClockPickupColliderComponent(3),
                new RotationComponent(0, RandomNumberGenerator.getFloat(0, 360), 0),
                modelComponent
        );
    }

}
