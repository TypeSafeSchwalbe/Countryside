package devtaube.countryside.menu.pipelines;

import rosequartz.gfx.GraphicsPipeline;

public class ItemPipeline extends GraphicsPipeline {

    public ItemPipeline() {
        add(
                new GuiItemBehavior(),
                new TextureItemBehavior(),
                new ClickItemBehavior()
        );
    }

}
