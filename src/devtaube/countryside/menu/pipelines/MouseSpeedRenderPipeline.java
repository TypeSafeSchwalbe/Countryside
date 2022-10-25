package devtaube.countryside.menu.pipelines;

import devtaube.countryside.level.pipelines.PlayerRotationBehavior;
import devtaube.countryside.menu.MenuFont;
import rosequartz.gfx.GraphicsPipeline;

public class MouseSpeedRenderPipeline extends GraphicsPipeline {

    private static final MenuFont RENDER_FONT = new MenuFont();

    public MouseSpeedRenderPipeline() {
        add(() -> {
            float textHeight = 0.05f / 2;
            float textX = 0.025f + textHeight * 1.25f + textHeight / 7;
            float textY = textX * 1.15f;
            RENDER_FONT.render("Look speed: " + Math.floor(PlayerRotationBehavior.SPEED * 10) / 10, textX, textY, textHeight, textHeight / 7, textHeight / 7);
        });
    }

}
