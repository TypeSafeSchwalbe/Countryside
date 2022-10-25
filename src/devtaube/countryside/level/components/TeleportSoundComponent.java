package devtaube.countryside.level.components;

import rosequartz.afx.Audio;
import rosequartz.afx.AudioSource;
import rosequartz.ecb.Component;
import rosequartz.files.Resource;
import rosequartz.rng.RandomNumberGenerator;

public class TeleportSoundComponent implements Component {

    public static final Audio TELEPORT_SOUND = new Audio(new Resource("sounds/teleport.ogg"));
    public AudioSource teleportSoundSource = new AudioSource().setVolume(10f);

    public void play(float x, float y, float z) {
        teleportSoundSource
                .setPosition(x, y, z)
                .setPitch(1 + RandomNumberGenerator.getFloat(-0.25f, 0.25f))
                .play(TELEPORT_SOUND);
    }

}
