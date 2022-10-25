package devtaube.countryside.level.pipelines;

import rosequartz.afx.Audio;
import rosequartz.afx.AudioSource;
import rosequartz.ecb.Pipeline;
import rosequartz.files.Resource;

public class AmbiencePipeline extends Pipeline {

    private static final Audio PEACEFUL = new Audio(new Resource("sounds/ambience.ogg"));
    private static final Audio HOSTILE = new Audio(new Resource("sounds/enemy.ogg"));

    private static AudioSource peacefulSource;
    private static AudioSource hostileSource;

    public static float hostility = 0.0f; // 0 = peaceful, 1 = hostile (until 0.5 fade out peaceful, from 0.5 fade in hostile)

    public AmbiencePipeline() {
        hostility = 0;
        if(peacefulSource == null) peacefulSource = new AudioSource().repeat(true).setVolume(0).play(PEACEFUL);
        if(hostileSource == null) hostileSource = new AudioSource().repeat(true).setVolume(0).play(HOSTILE);
        add(() -> {
            peacefulSource.setVolume(Math.max(1 - hostility * 2, 0));
            hostileSource.setVolume(Math.max(hostility * 2 - 1, 0));
        });
    }

}
