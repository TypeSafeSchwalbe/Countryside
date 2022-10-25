package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;

public class Enemy extends Entity {

    public Enemy(float x, float y, float z) {
        ModelComponent modelComponent = new ModelComponent("models/enemy.obj", "models/enemy.png", "models/enemy_bright.png");
        modelComponent.useAmbientLight = false;
        add(
                new PositionComponent(x, y, z),
                new RotationComponent(),
                new TeleportSoundComponent(),
                new EnemyRotationComponent(),
                new EnemyMovementComponent(),
                new EnemyVisibilityBlockComponent(),
                modelComponent
        );
    }

}
