package devtaube.countryside.level.components;

import rosequartz.ecb.Component;

public class ScaleComponent implements Component {

    public float s = 1;

    public ScaleComponent() {}

    public ScaleComponent(float s) {
        this.s = s;
    }


    @Override
    public Component copy() { return new ScaleComponent(s); }

}
