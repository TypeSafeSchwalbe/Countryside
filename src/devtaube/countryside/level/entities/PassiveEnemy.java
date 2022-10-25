package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;

public class PassiveEnemy extends Entity {

    public PassiveEnemy(float x, float y, float z) {
        ModelComponent modelComponent = new ModelComponent("models/enemy.obj", "models/enemy.png", "models/enemy_bright.png");
        modelComponent.useAmbientLight = false;
        TeleportSoundComponent teleportSoundComponent = new TeleportSoundComponent();
        teleportSoundComponent.teleportSoundSource.setVolume(1);
        teleportSoundComponent.play(x, y, z);
        add(
                new PositionComponent(x, y, z),
                new RotationComponent(),
                teleportSoundComponent,
                new EnemyRotationComponent(),
                modelComponent
        );
    }

}
