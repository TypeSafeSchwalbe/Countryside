package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.*;
import devtaube.countryside.level.entities.Enemy;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Pipeline;

public class DeathTriggerPipeline extends Pipeline {

    public static final float REMOVAL_DISTANCE = 25;
    public static final float REMOVAL_ENEMY_SPAWN_DISTANCE = EnemyVisibilityBlockComponent.BLOCKED_DISTANCE;
    public static final float MAX_ANGLE_DIFFERENCE = 75;

    public DeathTriggerPipeline() {
        // if distance to trigger is more than the trigger removal distance, remove it
        add(() -> ECB.get(DeathTriggerComponent.class, (player, deathTriggerComponent) -> {
            PositionComponent playerPositionComponent = player.get(PositionComponent.class);
            float distance = (float) Math.sqrt(Math.pow(playerPositionComponent.x - deathTriggerComponent.x, 2) + Math.pow(playerPositionComponent.z - deathTriggerComponent.z, 2));
            if(distance > REMOVAL_DISTANCE) {
                player.remove(DeathTriggerComponent.class);
                RotationComponent playerRotationComponent = player.get(RotationComponent.class);
                float playerLookX = (float) Math.cos(Math.toRadians(playerRotationComponent.y));
                float playerLookZ = (float) Math.sin(Math.toRadians(playerRotationComponent.y));
                ECB.add(new Enemy(
                        playerPositionComponent.x + playerLookX * REMOVAL_ENEMY_SPAWN_DISTANCE,
                        0,
                        playerPositionComponent.z + playerLookZ * REMOVAL_ENEMY_SPAWN_DISTANCE
                ));
            }
        }));
        // configure hostility
        add(() -> ECB.get(DeathTriggerComponent.class, (player, deathTriggerComponent) -> {
            PositionComponent playerPosition = player.get(PositionComponent.class);
            AmbiencePipeline.hostility = 1 - (float) Math.sqrt(Math.pow(playerPosition.x - deathTriggerComponent.x, 2) + Math.pow(playerPosition.z - deathTriggerComponent.z, 2)) / EnemyPipeline.HOSTILITY_DISTANCE;
        }));
        // if angle matches, add death component
        add(() -> ECB.get(DeathTriggerComponent.class, (player, deathTriggerComponent) -> {
            RotationComponent playerRotationComponent = player.get(RotationComponent.class);
            float angleDifference = Math.abs(playerRotationComponent.y - deathTriggerComponent.deathAngle);
            if(angleDifference > 180) angleDifference = 360 - angleDifference;
            if(angleDifference > MAX_ANGLE_DIFFERENCE) return;
            player.remove(deathTriggerComponent);
            ECB.get(NotePickupColliderComponent.class, (groundNote, ignored) -> ECB.remove(groundNote));
            player.get(PlayerRotationComponent.class, (PlayerRotationComponent playerRotationControlComponent) -> playerRotationControlComponent.enabled = false);
            player.get(PlayerMovementComponent.class, (PlayerMovementComponent playerMovementControlComponent) -> playerMovementControlComponent.enabled = false);
            player.add(new DeathComponent());
        }));
    }

}
