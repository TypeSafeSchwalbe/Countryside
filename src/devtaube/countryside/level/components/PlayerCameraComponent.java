package devtaube.countryside.level.components;

import rosequartz.ecb.Component;
import rosequartz.gfx.CameraConfiguration;
import rosequartz.gfx.PerspectiveCamera;

public class PlayerCameraComponent implements Component {

    public PerspectiveCamera camera;

    public PlayerCameraComponent() {
        camera = new PerspectiveCamera(new CameraConfiguration())
                .setClipPlanes(0.1f, 1000)
                .setFieldOfViewDegrees(60);
    }

    @Override
    public Component copy() {
        PlayerCameraComponent clone = new PlayerCameraComponent();
        clone.camera = camera.clone();
        return clone;
    }

}
