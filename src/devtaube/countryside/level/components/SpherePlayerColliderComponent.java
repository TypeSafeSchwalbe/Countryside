package devtaube.countryside.level.components;

import rosequartz.coll.Collider;
import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Component;

public class SpherePlayerColliderComponent implements Component {

    public final SphereCollider[] colliders;

    public SpherePlayerColliderComponent(float radius) {
        colliders = new SphereCollider[] { new SphereCollider(0, 0, 0, radius) };
    }

    public SpherePlayerColliderComponent(SphereCollider[] colliders) {
        this.colliders = colliders;
    }

    public boolean getColliding(Collider collider) {
        boolean collision = false;
        for(SphereCollider sphereCollider: colliders) if(sphereCollider.getColliding(collider)) collision = true;
        return collision;
    }

    public void setPositionIfSingle(float x, float y, float z) {
        if(colliders.length == 1) colliders[0].setPosition(x, y, z);
    }

}
