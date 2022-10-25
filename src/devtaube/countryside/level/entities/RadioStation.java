package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.Entity;

public class RadioStation extends Entity {

    public RadioStation(float x, float z, int parentTileX, int parentTileZ) {
        add(
                new PositionComponent(x, 0, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new ScaleComponent(1 * 4.272f / 2),
                new ModelComponent("models/radio_station.obj", "models/radio_station.png", "models/radio_station_bright.png"),
                new SpherePlayerColliderComponent(6)
        );
    }

}
