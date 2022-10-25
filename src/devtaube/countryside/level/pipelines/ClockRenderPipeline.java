package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.ClockRenderComponent;
import rosequartz.ecb.ECB;
import rosequartz.files.Resource;
import rosequartz.gfx.*;

import static rosequartz.RoseQuartz.*;

public class ClockRenderPipeline extends GraphicsPipeline {

    private static final Texture CLOCK_TEXTURE = new Texture(new Resource("models/clock.png"));
    private static final ShaderProgram CLOCK_SHADER = new ShaderProgram(new Resource("shaders/texture_item_vertex.glsl"), new Resource("shaders/texture_item_fragment.glsl"))
            .setUniformMatrix4("PROJECTION_VIEW_MATRIX", new OrthographicCamera(new CameraConfiguration().setPosition(0, 0, 1)).setBounds(0, 1, 0, 1).getProjectionViewMatrix())
            .setUniformTexture("TEXTURE_SAMPLER", CLOCK_TEXTURE);

    public static final float ANIMATION_OFFSET = 0.05f;

    private final VertexArray clockArray = new VertexArray(2, 2);

    public ClockRenderPipeline() {
        add(() -> ECB.get(ClockRenderComponent.class, (player, clockRenderComponent) -> {
            clockRenderComponent.time += deltaTime();
            if(clockRenderComponent.time > 4) player.remove(clockRenderComponent);
            DepthTestingManager.get().setEnabled(false);
            CLOCK_SHADER.select();
            float clockHeight = 0.5f;
            float clockWidth = clockHeight * 5 / 8 * Graphics.windowHeight() / Graphics.windowWidth();
            float clockX = (1 - clockWidth) / 2;
            float clockY = (1 - clockHeight) / 2 + (float) Math.sin(clockRenderComponent.time * 2) * ANIMATION_OFFSET;
            clockArray.clear()
                    .vertex( clockX,              clockY,                 0, 0 ) // [2]-----[3]
                    .vertex( clockX + clockWidth, clockY,                 1, 0 ) //  |\___132|
                    .vertex( clockX,              clockY + clockHeight,   0, 1 ) //  |012 \__|
                    .vertex( clockX + clockWidth, clockY + clockHeight,   1, 1 ) // [0]-----[1]
                    .fragment( 0, 1, 2 )
                    .fragment( 1, 3, 2 )
                    .upload()
                    .render();
        }));
    }

}
