package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.PlayerCameraComponent;
import devtaube.countryside.level.components.PlayerComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.RotationComponent;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;
import rosequartz.gfx.PerspectiveCamera;
import rosequartz.gfx.RenderTarget;

public class PlayerCameraConfigurationBehavior implements Behavior {

    @Override
    public void run() {
        ECB.<PlayerCameraComponent>get(PlayerCameraComponent.class, (playerCamera, playerCameraComponent) -> ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> {
            PerspectiveCamera camera = playerCamera.<PlayerCameraComponent>get(PlayerCameraComponent.class).camera;
            camera.setAspectRatio(RenderTarget.getDefault().getWidth(), RenderTarget.getDefault().getHeight());
            player.<PositionComponent>get(PositionComponent.class, positionComponent -> camera.getConfiguration().setPosition(positionComponent.x, positionComponent.y + 1.5f, positionComponent.z));
            player.<RotationComponent>get(RotationComponent.class, rotationComponent -> {
                float lookX = (float) (Math.cos(Math.toRadians(rotationComponent.y)) * Math.cos(Math.toRadians(rotationComponent.z)));
                float lookY = (float) Math.sin(Math.toRadians(rotationComponent.z));
                float lookZ = (float) (Math.sin(Math.toRadians(rotationComponent.y)) * Math.cos(Math.toRadians(rotationComponent.z)));
                camera.getConfiguration().setLookIn(lookX, lookY, lookZ, 0, 1, 0);
            });
        }));
    }

}
