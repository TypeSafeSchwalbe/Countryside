package devtaube.countryside.level.components;

import rosequartz.ecb.Component;

public class TilePositionComponent implements Component {

    public int x;
    public int z;

    public TilePositionComponent(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public Component copy() { return new TilePositionComponent(x, z); }

}
