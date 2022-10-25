package devtaube.countryside.menu.entities;

import devtaube.countryside.level.components.PlayerCameraComponent;
import rosequartz.ecb.Entity;

public class MenuPlayerCamera extends Entity {

    public MenuPlayerCamera() { add(new PlayerCameraComponent()); }

}
