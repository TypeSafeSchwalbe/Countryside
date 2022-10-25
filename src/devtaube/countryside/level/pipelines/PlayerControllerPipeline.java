package devtaube.countryside.level.pipelines;

import rosequartz.gfx.GraphicsPipeline;

public class PlayerControllerPipeline extends GraphicsPipeline {

    public static final float STICK_DEADZONE = 0.15f;

    public PlayerControllerPipeline() {
        add(
                new PlayerRotationBehavior(),
                new PlayerMovementBehavior(),
                new PlayerAudioListenerBehavior()
        );
    }

}
