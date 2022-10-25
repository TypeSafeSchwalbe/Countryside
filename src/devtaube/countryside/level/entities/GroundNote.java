package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;
import rosequartz.gfx.VertexArray;
import rosequartz.rng.RandomNumberGenerator;

public class GroundNote extends Entity {

    private static VertexArray generateVertexArray() {
        return new VertexArray(3, 3, 2)
                .vertex(  0.178f, 0, -0.3115f,   0, 1, 0,   0, 0 )
                .vertex( -0.178f, 0, -0.3115f,   0, 1, 0,   1, 0 )
                .vertex(  0.178f, 0,  0.3115f,   0, 1, 0,   0, 1 )
                .vertex( -0.178f, 0,  0.3115f,   0, 1, 0,   1, 1 )
                .fragment( 0, 1, 2 )
                .fragment( 1, 3, 2 )
                .upload();
    }

    public GroundNote(float x, float z, int parentTileX, int parentTileZ) {
        ModelComponent modelComponent = new ModelComponent(generateVertexArray(), "notes/ground.png", "notes/ground_bright.png");
        modelComponent.useAmbientLight = false;
        add(
                new PositionComponent(x, 0.01f, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new NotePickupColliderComponent(3),
                new RotationComponent(0, RandomNumberGenerator.getFloat(0, 360), 0),
                modelComponent
        );
    }

}
