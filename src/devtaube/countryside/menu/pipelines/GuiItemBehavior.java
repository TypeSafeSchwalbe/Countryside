package devtaube.countryside.menu.pipelines;

import devtaube.countryside.menu.components.GuiItemComponent;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;

public class GuiItemBehavior implements Behavior {

    @Override
    public void run() {
        ECB.<GuiItemComponent>get(GuiItemComponent.class, (guiItem, guiItemComponent) -> guiItemComponent.calculate());
    }

}
