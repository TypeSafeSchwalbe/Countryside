package devtaube.countryside.level.components;

import rosequartz.ecb.Component;

// if the player matches the given values, he dies.
public class DeathTriggerComponent implements Component {

    public final float deathAngle;
    public final float x;
    public final float z;

    public DeathTriggerComponent(float deathAngle, float x, float z) {
        this.deathAngle = deathAngle;
        this.x = x;
        this.z = z;
    }

}
