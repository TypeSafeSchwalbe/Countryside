package devtaube.countryside.menu.entities;

import devtaube.countryside.level.components.PlayerComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.RotationComponent;
import rosequartz.ecb.Entity;

public class MenuPlayer extends Entity {

    public MenuPlayer(float x, float y, float z, float rotationX, float rotationY, float rotationZ) {
        add(
                new PlayerComponent(),
                new PositionComponent(x, y, z),
                new RotationComponent(rotationX, rotationY, rotationZ)
        );
    }

}
