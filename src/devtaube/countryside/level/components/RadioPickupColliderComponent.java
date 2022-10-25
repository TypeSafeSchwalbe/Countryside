package devtaube.countryside.level.components;

import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Component;

public class RadioPickupColliderComponent implements Component {

    public final SphereCollider collider;

    public RadioPickupColliderComponent(float radius) { collider = new SphereCollider(0, 0, 0, radius); }

}
