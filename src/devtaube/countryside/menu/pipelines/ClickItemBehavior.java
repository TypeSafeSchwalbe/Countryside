package devtaube.countryside.menu.pipelines;

import devtaube.countryside.menu.components.ClickableItemComponent;
import devtaube.countryside.menu.components.HoverTextureItemComponent;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;
import rosequartz.input.InputManager;
import rosequartz.input.MouseButton;

public class ClickItemBehavior implements Behavior {

    private static boolean lastFrameClicked = false;

    @Override
    public void run() {
        boolean clicked = InputManager.get().mouseButton(MouseButton.LEFT);
        ECB.<ClickableItemComponent>get(ClickableItemComponent.class, (clickable, clickableItemComponent) -> clickable.get(HoverTextureItemComponent.class, (HoverTextureItemComponent hoverTextureItemComponent) -> {
            if(clicked && !lastFrameClicked && hoverTextureItemComponent.hovering) clickableItemComponent.onClick.run();
        }));
        lastFrameClicked = clicked;
    }

}
