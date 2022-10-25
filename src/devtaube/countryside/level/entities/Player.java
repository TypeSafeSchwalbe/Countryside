package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;

public class Player extends Entity {

    public Player(float x, float y, float z) {
        add(
                new PlayerComponent(),
                new PlayerMovementComponent(),
                new PlayerRotationComponent(),
                new PositionComponent(x, y, z),
                new RotationComponent(),
                new FadeOutComponent()
        );
    }

}
