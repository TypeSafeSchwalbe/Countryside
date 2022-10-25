package devtaube.countryside.level.pipelines;

import rosequartz.gfx.GraphicsPipeline;
import rosequartz.gfx.RenderTarget;
import rosequartz.gfx.Texture;

public class LowResRender {

    public static final int RESOLUTION_HEIGHT = 192; // resolution of dsi is 256:192px

    public static RenderTarget lowResTarget;

    public static class LowResPreparePipeline extends GraphicsPipeline {

        public LowResPreparePipeline() {
            createTarget();
            add(() -> {
                if(!correctDimensions()) createTarget();
                lowResTarget.clearColor(0.1f, 0.1f, 0.1f, 1)
                        .clearDepth(1)
                        .target();
            });
        }

    }

    public static class LowResRenderPipeline extends GraphicsPipeline {

        public LowResRenderPipeline() {
            add(() -> {
                RenderTarget.getDefault().target();
                lowResTarget.getTexture().blit(0, 0, 1, 1, 0, 0, 1, 1);
            });
        }

    }

    private static int getCorrectWidth() {
        return Math.round(RESOLUTION_HEIGHT / (float) RenderTarget.getDefault().getHeight() * RenderTarget.getDefault().getWidth());
    }

    private static boolean correctDimensions() {
        return lowResTarget.getWidth() == getCorrectWidth();
    }

    private static void createTarget() {
        lowResTarget = new RenderTarget(new Texture(getCorrectWidth(), RESOLUTION_HEIGHT));
        lowResTarget.target();
    }

}
