package devtaube.countryside.level.pipelines;

import rosequartz.RoseQuartz;
import rosequartz.files.Resource;
import rosequartz.gfx.Graphics;
import rosequartz.gfx.GraphicsPipeline;
import rosequartz.gfx.RenderTarget;
import rosequartz.gfx.Texture;
import rosequartz.input.InputManager;
import rosequartz.math.Vec2;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerTouchInputPipeline extends GraphicsPipeline {

    private static final Texture VSTICK_TOP = new Texture(new Resource("menu/vstick_top.png"));
    private static final Texture VSTICK_BOTTOM = new Texture(new Resource("menu/vstick_bottom.png"));

    public static final float TOUCH_INPUT_HEIGHT = 0.5f;

    public static int x = 0; // left: -1, right: 1, none: 0
    public static int y = 0; // top: -1, bottom: 1, none: 0

    public PlayerTouchInputPipeline() {
        add(() -> {
            if(!InputManager.get().hasTouchInput()) return;
            float touchInputWidth = TOUCH_INPUT_HEIGHT * RenderTarget.getDefault().getHeight() / RenderTarget.getDefault().getWidth();
            // bottom part
            VSTICK_BOTTOM.blit(
        /* src1  */ 0, 0,
        /* src2  */ 1, 1,
        /* dest1 */ touchInputWidth * 0.333f, 0,
        /* dest2 */ touchInputWidth * 0.666f, TOUCH_INPUT_HEIGHT * 0.333f
            );
            // top part
            // get touch position in UV
            AtomicReference<Vec2> touchInsideStick = new AtomicReference<>();
            InputManager.get().forTouch((touch) -> {
                if(!inTouchInputArea(touch)) return;
                touchInsideStick.set(translateUV(touch));
            });
            // get relative position of stick
            Vec2 stickCenter = new Vec2(touchInputWidth * 0.5f, TOUCH_INPUT_HEIGHT * 0.5f); // default
            if(touchInsideStick.get() != null) // overwrite with touch position if inside stick
                stickCenter = touchInsideStick.get();
            // draw stick
            VSTICK_TOP.blit(
        /* src1  */ 0, 0,
        /* src2  */ 1, 1,
        /* dest1 */ stickCenter.x - touchInputWidth * 0.15f, stickCenter.y - TOUCH_INPUT_HEIGHT * 0.15f,
        /* dest2 */ stickCenter.x + touchInputWidth * 0.15f, stickCenter.y + TOUCH_INPUT_HEIGHT * 0.15f
            );
            // calculate value
            Vec2 stickValue = stickCenter.divN(touchInputWidth, TOUCH_INPUT_HEIGHT); // value now from 0..1
            x = stickValue.x < 0.5f - 0.15f? -1 : stickValue.x > 0.5f + 0.15f? 1 : 0;
            y = stickValue.y < 0.5f - 0.15f? 1 : stickValue.y > 0.5f + 0.15f? -1 : 0;
        });
    }

    public static Vec2 translateUV(Vec2 px) {
        return new Vec2(px.x, Graphics.windowHeight()) // x=[0..vw], y=vh
                .subN(0, px.y) // x=[0..vw], y=[vh..0]
                .div(Graphics.windowSize());
    }

    public static boolean inTouchInputArea(Vec2 px) {
        float touchInputWidth = TOUCH_INPUT_HEIGHT * RenderTarget.getDefault().getHeight() / RenderTarget.getDefault().getWidth();
        Vec2 uv = translateUV(px);
        return 0 <= uv.x && uv.x <= touchInputWidth && 0 <= uv.y && uv.y < TOUCH_INPUT_HEIGHT;
    }

}
