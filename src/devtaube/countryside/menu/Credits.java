package devtaube.countryside.menu;

import devtaube.countryside.menu.components.ClickableItemComponent;
import devtaube.countryside.menu.components.GuiItemComponent;
import devtaube.countryside.menu.components.HoverTextureItemComponent;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Entity;

public class Credits {

    public static void start() {
        Menu.pipelines();
        Menu.entities();
        gui();
    }

    private static void gui() {
        ECB.add(new Entity().add(
                new GuiItemComponent().size(0, 0.9).sizeRelativeX(1.75).position(0.5, 0.5).translate(-0.5, -0.5),
                new HoverTextureItemComponent("menu/credits.png", "menu/credits.png"),
                new ClickableItemComponent(Menu::start)
        ));
    }

}
