package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.PlayerMovementComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.RotationComponent;
import devtaube.countryside.level.components.SpherePlayerColliderComponent;
import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;
import rosequartz.input.Gamepad;
import rosequartz.input.InputManager;
import rosequartz.input.Key;

import static rosequartz.RoseQuartz.deltaTime;

public class PlayerMovementBehavior implements Behavior {

    static { PromptRenderPipeline.registerPrompt("player-move", "[W][A][S][D]/(L) move"); }

    @Override
    public void run() {
        ECB.get(PlayerMovementComponent.class, (player, playerMovementComponent) -> {
            if(!playerMovementComponent.enabled) return;
            float speed = playerMovementComponent.speed;
            player.get(RotationComponent.class, rotationComponent -> player.<PositionComponent>get(PositionComponent.class, positionComponent -> {
                PromptRenderPipeline.showPrompt("player-move");
                float lookX = (float) Math.cos(Math.toRadians(rotationComponent.y));
                float lookZ = (float) Math.sin(Math.toRadians(rotationComponent.y));
                float lookL = (float) Math.sqrt(Math.pow(lookX, 2) + Math.pow(lookZ, 2));
                lookX /= lookL;
                lookZ /= lookL;
                float movementX = 0;
                float movementZ = 0;
                float movementSpeedMultiplier = 1f;
                if(InputManager.get().key(Key.W) ||
                        InputManager.get().mainGamepad().getAxis(Gamepad.Axis.LEFT_STICK_Y) < -PlayerControllerPipeline.STICK_DEADZONE ||
                        PlayerTouchInputPipeline.y == -1
                ) {
                    movementX += lookX;
                    movementZ += lookZ;
                }
                if(InputManager.get().key(Key.A) ||
                        InputManager.get().mainGamepad().getAxis(Gamepad.Axis.LEFT_STICK_X) < -PlayerControllerPipeline.STICK_DEADZONE ||
                        PlayerTouchInputPipeline.x == -1
                ) {
                    movementX += lookZ;
                    movementZ -= lookX;
                    movementSpeedMultiplier = 0.75f;
                }
                if(InputManager.get().key(Key.D) ||
                        InputManager.get().mainGamepad().getAxis(Gamepad.Axis.LEFT_STICK_X) > PlayerControllerPipeline.STICK_DEADZONE ||
                        PlayerTouchInputPipeline.x == 1
                ) {
                    movementX -= lookZ;
                    movementZ += lookX;
                    movementSpeedMultiplier = 0.75f;
                }
                if(InputManager.get().key(Key.S) ||
                        InputManager.get().mainGamepad().getAxis(Gamepad.Axis.LEFT_STICK_Y) > PlayerControllerPipeline.STICK_DEADZONE ||
                        PlayerTouchInputPipeline.y == 1
                ) {
                    movementX -= lookX;
                    movementZ -= lookZ;
                    movementSpeedMultiplier = 0.5f;
                }
                float movementL = (float) Math.sqrt(Math.pow(movementX, 2) + Math.pow(movementZ, 2));
                if(movementL <= 0.01) return;
                PromptRenderPipeline.stopPrompt("player-move");
                movementX = movementX / movementL * movementSpeedMultiplier;
                movementZ = movementZ / movementL * movementSpeedMultiplier;
                if(!collides((float) (positionComponent.x + movementX * speed * deltaTime()), positionComponent.y, positionComponent.z) || collides(positionComponent.x, positionComponent.y, positionComponent.z))
                    positionComponent.x += movementX * speed * deltaTime();
                if(!collides(positionComponent.x, positionComponent.y, (float) (positionComponent.z + movementZ * speed * deltaTime())) || collides(positionComponent.x, positionComponent.y, positionComponent.z))
                    positionComponent.z += movementZ * speed * deltaTime();
            }));
        });
    }

    private static boolean collides(float x, float y, float z) {
        SphereCollider playerCollider = new SphereCollider(x, y, z, 0.2f);
        boolean[] collision = new boolean[] { false };
        ECB.<SpherePlayerColliderComponent>get(SpherePlayerColliderComponent.class, (entity, spherePlayerColliderComponent) -> {
            if(spherePlayerColliderComponent.getColliding(playerCollider)) collision[0] = true;
        });
        return collision[0];
    }

}
