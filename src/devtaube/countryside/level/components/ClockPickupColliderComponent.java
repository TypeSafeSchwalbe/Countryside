package devtaube.countryside.level.components;

import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Component;

public class ClockPickupColliderComponent implements Component {

    public final SphereCollider collider;

    public ClockPickupColliderComponent(float radius) { collider = new SphereCollider(0, 0, 0, radius); }

}
