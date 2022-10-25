package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.Level;
import devtaube.countryside.level.components.DeathComponent;
import rosequartz.afx.Audio;
import rosequartz.afx.AudioSource;
import rosequartz.ecb.ECB;
import rosequartz.files.Resource;
import rosequartz.gfx.*;

import static rosequartz.RoseQuartz.*;

public class DeathPipeline extends GraphicsPipeline {

    private static final Texture CLOSE_UP_TEXTURE = new Texture(new Resource("models/enemy_closeup.png"));
    private static final ShaderProgram CLOSE_UP_SHADER = new ShaderProgram(new Resource("shaders/texture_item_vertex.glsl"), new Resource("shaders/texture_item_fragment.glsl"))
            .setUniformMatrix4("PROJECTION_VIEW_MATRIX", new OrthographicCamera(new CameraConfiguration().setPosition(0, 0, 1)).setBounds(0, 1, 0, 1).getProjectionViewMatrix())
            .setUniformTexture("TEXTURE_SAMPLER", CLOSE_UP_TEXTURE);

    private static final ShaderProgram BLACK_SHADER = new ShaderProgram(new Resource("shaders/fill_black_vertex.glsl"), new Resource("shaders/fill_black_fragment.glsl"));
    private static final VertexArray BLACK_ARRAY = new VertexArray(2)
            .vertex( -1, -1 ) // [2]-----[3]
            .vertex(  1, -1 ) //  |\___132|
            .vertex( -1,  1 ) //  |012 \__|
            .vertex(  1,  1 ) // [0]-----[1]
            .fragment( 0, 1, 2 )
            .fragment( 1, 3, 2 )
            .upload();

    private static final Audio ENEMY_SCREAM = new Audio(new Resource("sounds/enemy_scream.ogg"));
    private static final AudioSource ENEMY_SCREAM_SOURCE = new AudioSource();

    private final VertexArray closeUpArray = new VertexArray(2, 2);

    public DeathPipeline() {
        add(() -> ECB.<DeathComponent>get(DeathComponent.class, (player, deathComponent) -> {
            // 'die' after 4 seconds
            if(deathComponent.time > 4) Level.death();
            // scream sound
            if(deathComponent.time == 0) ENEMY_SCREAM_SOURCE.play(ENEMY_SCREAM);
            // hostility
            if(deathComponent.time > 1 && AmbiencePipeline.hostility > 0.5) AmbiencePipeline.hostility -= deltaTime() / 2 / 2;
            // draw white background
            DepthTestingManager.get().setEnabled(false);
            BLACK_SHADER.setUniformFloat("ALPHA", Math.min(Math.max(deathComponent.time - 1, 0) / 2, 1)) // fade out (after 1 second) for 2 seconds
                    .select();
            BLACK_ARRAY.render();
            // increase time of death component
            deathComponent.time += deltaTime();
            // close up of monster
            CLOSE_UP_SHADER.select();
            float enemyHeight = Graphics.windowWidth() / Graphics.windowHeight();
            closeUpArray.clear()
                    .vertex( 0, 1 - enemyHeight,   0, 0 ) // [2]-----[3]
                    .vertex( 1, 1 - enemyHeight,   1, 0 ) //  |\___132|
                    .vertex( 0, 1,                 0, 1 ) //  |012 \__|
                    .vertex( 1, 1,                 1, 1 ) // [0]-----[1]
                    .fragment( 0, 1, 2 )
                    .fragment( 1, 3, 2 )
                    .upload()
                    .render();
        }));
    }

}
