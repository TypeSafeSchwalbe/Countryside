package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.PlayerCameraComponent;
import rosequartz.ecb.Entity;

public class PlayerCamera extends Entity {

    public PlayerCamera() {
        add(new PlayerCameraComponent());
    }

}
