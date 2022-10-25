package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.PlayerRotationComponent;
import devtaube.countryside.level.components.RotationComponent;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;
import rosequartz.gfx.Graphics;
import rosequartz.gfx.RenderTarget;
import rosequartz.input.Gamepad;
import rosequartz.input.InputManager;
import rosequartz.input.MouseButton;
import rosequartz.math.Vec2;

import java.util.concurrent.atomic.AtomicReference;

import static rosequartz.RoseQuartz.*;

public class PlayerRotationBehavior implements Behavior {

    static { PromptRenderPipeline.registerPrompt("player-look-desktop", "<mouse>/(R) look around"); }
    static { PromptRenderPipeline.registerPrompt("player-look-mobile", "<touch> look around"); }

    private static float lastFrameMouseX = 0;
    private static float lastFrameMouseY = 0;

    private static boolean lastFrameTouch = false;
    private static float touchPositionAnchorX = 0;
    private static float touchPositionAnchorY = 0;
    private static float touchRotationAnchorY = 0;
    private static float touchRotationAnchorZ = 0;

    public static float SPEED = 0.5f;

    public static float CONTROLLER_LOOK_SPEED;

    @Override
    public void run() {
        CONTROLLER_LOOK_SPEED = SPEED * 75;
        boolean[] componentFound = {false};
        ECB.get(PlayerRotationComponent.class, (player, playerRotationComponent) -> {
            componentFound[0] = true;
            player.get(RotationComponent.class, rotationComponent -> {
                if(!Graphics.windowInFocus()) return;
                if(!playerRotationComponent.enabled) return;
                PromptRenderPipeline.showPrompt(InputManager.get().hasTouchInput()? "player-look-mobile" : "player-look-desktop");
                // controller
                float controllerRotationZ = 0;
                float controllerRotationY = 0;
                if(InputManager.get().mainGamepad().getAxis(Gamepad.Axis.RIGHT_STICK_Y) > PlayerControllerPipeline.STICK_DEADZONE)
                    controllerRotationZ -= CONTROLLER_LOOK_SPEED * Graphics.screenHeight() / Graphics.screenWidth() * deltaTime();
                if(InputManager.get().mainGamepad().getAxis(Gamepad.Axis.RIGHT_STICK_Y) < -PlayerControllerPipeline.STICK_DEADZONE)
                    controllerRotationZ += CONTROLLER_LOOK_SPEED * Graphics.screenHeight() / Graphics.screenWidth() * deltaTime();
                if(InputManager.get().mainGamepad().getAxis(Gamepad.Axis.RIGHT_STICK_X) > PlayerControllerPipeline.STICK_DEADZONE)
                    controllerRotationY += CONTROLLER_LOOK_SPEED * deltaTime();
                if(InputManager.get().mainGamepad().getAxis(Gamepad.Axis.RIGHT_STICK_X) < -PlayerControllerPipeline.STICK_DEADZONE)
                    controllerRotationY -= CONTROLLER_LOOK_SPEED * deltaTime();
                if(controllerRotationZ != 0 || controllerRotationY != 0) {
                    PromptRenderPipeline.stopPrompt("player-look-desktop");
                    PromptRenderPipeline.stopPrompt("player-look-mobile");
                }
                rotationComponent.z += controllerRotationZ;
                rotationComponent.y += controllerRotationY;
                // mouse input
                if(!InputManager.get().hasTouchInput() && (InputManager.get().mouseX() != lastFrameMouseX || InputManager.get().mouseY() != lastFrameMouseY)) {
                    if(lastFrameMouseX != 0 && lastFrameMouseY != 0) PromptRenderPipeline.stopPrompt("player-look-desktop");
                    float mouseRotationX = InputManager.get().mouseX() - (int) (RenderTarget.getDefault().getWidth() / 2f);
                    float mouseRotationY = -(InputManager.get().mouseY() - (int) (RenderTarget.getDefault().getHeight() / 2f));
                    InputManager.get().setMousePos(RenderTarget.getDefault().getWidth() / 2f, RenderTarget.getDefault().getHeight() / 2f);
                    lastFrameMouseX = InputManager.get().mouseX();
                    lastFrameMouseY = InputManager.get().mouseY();
                    rotationComponent.y += mouseRotationX * 90 * SPEED / Graphics.screenHeight() / 2;
                    rotationComponent.z += mouseRotationY * 90 * SPEED / Graphics.screenHeight() / 2;
                }
                // touch input
                if(InputManager.get().hasTouchInput()) {
                    AtomicReference<Vec2> touch = new AtomicReference<>();
                    InputManager.get().forTouch((sTouch) -> {
                        if(PlayerTouchInputPipeline.inTouchInputArea(sTouch)) return;
                        touch.set(sTouch);
                    });
                    if(touch.get() != null) {
                        if(!lastFrameTouch) {
                            touchPositionAnchorX = touch.get().x;
                            touchPositionAnchorY = touch.get().y;
                            touchRotationAnchorY = rotationComponent.y;
                            touchRotationAnchorZ = rotationComponent.z;
                        }
                        PromptRenderPipeline.stopPrompt("player-look-mobile");
                        float touchRotationX = touch.get().x - touchPositionAnchorX;
                        float touchRotationY = touch.get().y - touchPositionAnchorY;
                        float viewportMax = Math.max(Graphics.windowWidth(), Graphics.windowHeight());
                        rotationComponent.y = touchRotationAnchorY + (touchRotationX * 90 * SPEED / viewportMax);
                        rotationComponent.z = touchRotationAnchorZ - (touchRotationY * 90 * SPEED / viewportMax);
                    }
                    // last frame touch
                    lastFrameTouch = touch.get() != null;
                }
                // limit z rotation
                rotationComponent.z = Math.min(Math.max(rotationComponent.z, -85f), 85f);
            });
        });
        InputManager.get().setMouseVisible(!componentFound[0]);
    }

}
