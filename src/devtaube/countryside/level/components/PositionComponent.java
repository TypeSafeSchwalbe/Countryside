package devtaube.countryside.level.components;

import rosequartz.ecb.Component;

public class PositionComponent implements Component {

    public float x = 0;
    public float y = 0;
    public float z = 0;

    public PositionComponent() {}

    public PositionComponent(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Component copy() { return new PositionComponent(x, y, z); }

}
