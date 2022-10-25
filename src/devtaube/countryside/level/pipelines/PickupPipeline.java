package devtaube.countryside.level.pipelines;

import rosequartz.ecb.Pipeline;

public class PickupPipeline extends Pipeline {

    public PickupPipeline() {
        add(
                new NotePickupBehavior(),
                new RadioPickupBehavior(),
                new ClockPickupBehavior()
        );
    }

}
