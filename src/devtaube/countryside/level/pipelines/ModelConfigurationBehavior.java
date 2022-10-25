package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.ModelComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.RotationComponent;
import devtaube.countryside.level.components.ScaleComponent;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Entity;
import rosequartz.gfx.ModelInstance;

public class ModelConfigurationBehavior implements Behavior {

    @Override
    public void run() {
        ECB.get(ModelComponent.class, (Entity entity, ModelComponent modelComponent) -> {
            ModelInstance modelInstance = modelComponent.modelInstance.reset();
            entity.get(PositionComponent.class, (PositionComponent positionComponent) -> modelInstance.setPosition(positionComponent.x, positionComponent.y, positionComponent.z));
            entity.get(RotationComponent.class, (RotationComponent rotationComponent) -> modelInstance.rotateDegrees(rotationComponent.x, rotationComponent.y, rotationComponent.z));
            entity.get(ScaleComponent.class, (ScaleComponent scaleComponent) -> modelInstance.scale(scaleComponent.s));
        });
    }

}
