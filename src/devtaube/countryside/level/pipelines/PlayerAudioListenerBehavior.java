package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.PlayerCameraComponent;
import rosequartz.afx.AudioListener;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;

public class PlayerAudioListenerBehavior implements Behavior {

    @Override
    public void run() {
        ECB.<PlayerCameraComponent>get(PlayerCameraComponent.class, (playerCamera, playerCameraComponent) -> AudioListener.get().setConfiguration(playerCameraComponent.camera.getConfiguration()));
    }

}
