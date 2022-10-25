package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.SpherePlayerColliderComponent;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Pipeline;

public class ColliderConfigurationPipeline extends Pipeline {

    public ColliderConfigurationPipeline() {
        add(() -> ECB.get(SpherePlayerColliderComponent.class, (collider, spherePlayerColliderComponent) -> collider.get(PositionComponent.class, (PositionComponent positionComponent) -> spherePlayerColliderComponent.setPositionIfSingle(positionComponent.x, positionComponent.y, positionComponent.z))));
    }

}
