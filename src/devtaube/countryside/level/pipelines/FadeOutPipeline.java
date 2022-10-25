package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.*;
import devtaube.countryside.level.entities.Tile;
import rosequartz.ecb.ECB;
import rosequartz.files.Resource;
import rosequartz.gfx.DepthTestingManager;
import rosequartz.gfx.GraphicsPipeline;
import rosequartz.gfx.ShaderProgram;
import rosequartz.gfx.VertexArray;

public class FadeOutPipeline extends GraphicsPipeline {

    public static float FADEOUT_DISTANCE = Tile.TILE_SIZE / 1.5f;

    private static int time;

    private static final ShaderProgram FADE_OUT_SHADER = new ShaderProgram(new Resource("shaders/fill_vertex.glsl"), new Resource("shaders/fill_fragment.glsl"));
    private static final VertexArray FADE_OUT_V_ARRAY = new VertexArray(2)
            .vertex( -1, -1 ) // [2]-----[3]
            .vertex(  1, -1 ) //  |\___132|
            .vertex( -1,  1 ) //  |012 \__|
            .vertex(  1,  1 ) // [0]-----[1]
            .fragment( 0, 1, 2 )
            .fragment( 1, 3, 2 )
            .upload();

    public FadeOutPipeline() {
        // calculate fade out value
        add(() -> ECB.get(FadeOutComponent.class, (player, fadeOutComponent) -> {
            PositionComponent playerPositionComponent = player.get(PositionComponent.class);
            float[] closestDistance = { Float.MAX_VALUE };
            ECB.get(EnemyRotationComponent.class, (enemy, enemyRotationComponent) -> {
                PositionComponent enemyPositionComponent = enemy.get(PositionComponent.class);
                float distance = (float) Math.sqrt(Math.pow(playerPositionComponent.x - enemyPositionComponent.x, 2) + Math.pow(playerPositionComponent.z - enemyPositionComponent.z, 2));
                closestDistance[0] = Math.min(distance, closestDistance[0]);
            });
            ECB.get(DeathTriggerComponent.class, (ignored, deathTriggerComponent) -> {
                float distance = (float) Math.sqrt(Math.pow(playerPositionComponent.x - deathTriggerComponent.x, 2) + Math.pow(playerPositionComponent.z - deathTriggerComponent.z, 2));
                closestDistance[0] = Math.min(distance, closestDistance[0]);
            });
            ECB.get(DeathComponent.class, (ignored1, deathComponent) -> closestDistance[0] = FADEOUT_DISTANCE / 2);
            fadeOutComponent.fadeOut = Math.max(1 - closestDistance[0] / FADEOUT_DISTANCE, 0) / 3;
        }));
        // render fade out progress
        add(() -> ECB.get(FadeOutComponent.class, (player, fadeOutComponent) -> {
            DepthTestingManager.get().setEnabled(false);
            time = (time + 1) % 10000;
            FADE_OUT_SHADER.setUniformFloat("ALPHA", fadeOutComponent.fadeOut)
                    .setUniformInt("TIMESTEP", time)
                    .select();
            FADE_OUT_V_ARRAY.render();
        }));
    }

}
