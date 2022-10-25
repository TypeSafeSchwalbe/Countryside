package devtaube.countryside.level.components;

import rosequartz.ecb.Component;

public class RotationComponent implements Component {

    public float x = 0;
    public float y = 0;
    public float z = 0;

    public RotationComponent() {}

    public RotationComponent(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Component copy() { return new RotationComponent(x, y, z); }

}
