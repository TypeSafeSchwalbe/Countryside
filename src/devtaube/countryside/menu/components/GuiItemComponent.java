package devtaube.countryside.menu.components;

import rosequartz.ecb.Component;
import rosequartz.gfx.CameraConfiguration;
import rosequartz.gfx.OrthographicCamera;
import rosequartz.gfx.RenderTarget;

import java.util.ArrayList;

public class GuiItemComponent implements Component {

    public static final OrthographicCamera RENDERING_CAMERA = new OrthographicCamera(new CameraConfiguration().setPosition(0, 0, 1)).setBounds(0, 1, 1, 0);

    private static abstract class PositioningAction {
        public abstract void run(float x1, float y1, float x2, float y2);
        public abstract float getX1();
        public abstract float getY1();
        public abstract float getX2();
        public abstract float getY2();
    }

    private static class Reset extends PositioningAction {
        @Override public void run(float x1, float y1, float x2, float y2) {}
        @Override public float getX1() { return 0; }
        @Override public float getY1() { return 0; }
        @Override public float getX2() { return 0; }
        @Override public float getY2() { return 0; }
    }

    private static class SetPosition extends PositioningAction {
        private final float x, y;
        private float width, height;
        public SetPosition(float x, float y) { this.x = x; this.y = y; }
        @Override public void run(float x1, float y1, float x2, float y2) { width = x2 - x1; height = y2 - y1; }
        @Override public float getX1() { return x; }
        @Override public float getY1() { return y; }
        @Override public float getX2() { return x + width; }
        @Override public float getY2() { return y + height; }
    }

    private static class MovePosition extends PositioningAction {
        private float x, y, width, height;
        private final float dx, dy;
        public MovePosition(float x, float y) { dx = x; dy = y; }
        @Override public void run(float x1, float y1, float x2, float y2) { width = x2 - x1; height = y2 - y1; x = x1 + dx; y = y1 + dy; }
        @Override public float getX1() { return x; }
        @Override public float getY1() { return y; }
        @Override public float getX2() { return x + width; }
        @Override public float getY2() { return y + height; }
    }

    private static class TranslatePosition extends PositioningAction {
        private float x, y, width, height;
        private final float dx, dy;
        public TranslatePosition(float x, float y) { dx = x; dy = y; }
        @Override public void run(float x1, float y1, float x2, float y2) { width = x2 - x1; height = y2 - y1; x = x1 + width * dx; y = y1 + height * dy; }
        @Override public float getX1() { return x; }
        @Override public float getY1() { return y; }
        @Override public float getX2() { return x + width; }
        @Override public float getY2() { return y + height; }
    }

    private static class SetSize extends PositioningAction {
        private float x, y;
        private final float width, height;
        public SetSize(float width, float height) { this.width = width; this.height = height; }
        @Override public void run(float x1, float y1, float x2, float y2) { x = x1; y = y1; }
        @Override public float getX1() { return x; }
        @Override public float getY1() { return y; }
        @Override public float getX2() { return x + width; }
        @Override public float getY2() { return y + height; }
    }

    private static class SetSizeRelative extends PositioningAction {
        private float x, y;
        private float width, height;
        private final float dWidth, dHeight;
        private final boolean applyWidth, applyHeight;
        public SetSizeRelative(float width, boolean applyWidth, float height, boolean applyHeight) {
            dWidth = width; dHeight = height;
            this.applyWidth = applyWidth; this.applyHeight = applyHeight;
        }
        @Override public void run(float x1, float y1, float x2, float y2) {
            x = x1; y = y1;
            if(applyWidth) width = (y2 - y1) * RenderTarget.getCurrent().getHeight() / RenderTarget.getCurrent().getWidth() * dWidth;
            else width = (x2 - x1);
            if(applyHeight) height = (x2 - x1) * RenderTarget.getCurrent().getWidth() / RenderTarget.getCurrent().getHeight() * dHeight;
            else height = (y2 - y1);
        }
        @Override public float getX1() { return x; }
        @Override public float getY1() { return y; }
        @Override public float getX2() { return x + width; }
        @Override public float getY2() { return y + height; }
    }

    private final ArrayList<PositioningAction> positioningActions = new ArrayList<>();

    public float x1 = 0;
    public float y1 = 0;
    public float x2 = 0;
    public float y2 = 0;

    public GuiItemComponent() {}

    public GuiItemComponent reset() {
        positioningActions.add(new Reset());
        return this;
    }

    public GuiItemComponent position(double x, double y) {
        positioningActions.add(new SetPosition((float) x, (float) y));
        return this;
    }

    public GuiItemComponent move(double x, double y) {
        positioningActions.add(new MovePosition((float) x, (float) y));
        return this;
    }

    public GuiItemComponent translate(double x, double y) {
        positioningActions.add(new TranslatePosition((float) x, (float) y));
        return this;
    }

    public GuiItemComponent size(double width, double height) {
        positioningActions.add(new SetSize((float) width, (float) height));
        return this;
    }

    public GuiItemComponent sizeRelative(double width, boolean applyWidth, double height, boolean applyHeight) {
        positioningActions.add(new SetSizeRelative((float) width, applyWidth, (float) height, applyHeight));
        return this;
    }

    public GuiItemComponent sizeRelativeX(double width) {
        positioningActions.add(new SetSizeRelative((float) width, true, 0, false));
        return this;
    }

    public GuiItemComponent sizeRelativeY(double height) {
        positioningActions.add(new SetSizeRelative(0, false, (float) height, true));
        return this;
    }

    public void calculate() {
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 0;
        for(int actionIndex = 0; actionIndex < positioningActions.size(); actionIndex++) {
            PositioningAction action = positioningActions.get(actionIndex);
            action.run(x1, y1, x2, y2);
            x1 = action.getX1();
            y1 = action.getY1();
            x2 = action.getX2();
            y2 = action.getY2();
        }
    }

}
