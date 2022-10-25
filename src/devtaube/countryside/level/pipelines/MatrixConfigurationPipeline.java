package devtaube.countryside.level.pipelines;

import rosequartz.ecb.Pipeline;

public class MatrixConfigurationPipeline extends Pipeline {

    public MatrixConfigurationPipeline() {
        add(
                new PlayerCameraConfigurationBehavior(),
                new ModelConfigurationBehavior()
        );
    }

}
