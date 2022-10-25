package devtaube.countryside.level.components;

import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Component;

public class NotePickupColliderComponent implements Component {

    public final SphereCollider collider;

    public NotePickupColliderComponent(float radius) { collider = new SphereCollider(0, 0, 0, radius); }

}
