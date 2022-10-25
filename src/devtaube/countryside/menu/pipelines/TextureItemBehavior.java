package devtaube.countryside.menu.pipelines;

import devtaube.countryside.menu.components.GuiItemComponent;
import devtaube.countryside.menu.components.HoverTextureItemComponent;
import devtaube.countryside.menu.components.TextureItemComponent;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;
import rosequartz.files.Resource;
import rosequartz.gfx.DepthTestingManager;
import rosequartz.gfx.Graphics;
import rosequartz.gfx.ShaderProgram;
import rosequartz.gfx.VertexArray;
import rosequartz.input.InputManager;

public class TextureItemBehavior implements Behavior {

    private static final ShaderProgram textureItemShader = new ShaderProgram(new Resource("shaders/texture_item_vertex.glsl"), new Resource("shaders/texture_item_fragment.glsl"))
            .setUniformMatrix4("PROJECTION_VIEW_MATRIX", GuiItemComponent.RENDERING_CAMERA.getProjectionViewMatrix());
    private static final VertexArray textureItemArray = new VertexArray(2, 2);

    @Override
    public void run() {
        DepthTestingManager.get().setEnabled(false);
        textureItemShader.select();
        ECB.<TextureItemComponent>get(TextureItemComponent.class, (textureItem, textureItemComponent) -> textureItem.<GuiItemComponent>get(GuiItemComponent.class, guiItem -> {
            textureItemShader.setUniformTexture("TEXTURE_SAMPLER", textureItemComponent.texture);
            textureItemArray.clear()
                    .vertex( guiItem.x1, guiItem.y1,   0, 1 ) // [0]-----[1]
                    .vertex( guiItem.x2, guiItem.y1,   1, 1 ) //  |\___013|
                    .vertex( guiItem.x1, guiItem.y2,   0, 0 ) //  |023 \__|
                    .vertex( guiItem.x2, guiItem.y2,   1, 0 ) // [2]-----[3]
                    .fragment( 0, 2, 3 )
                    .fragment( 0, 1, 3 )
                    .upload()
                    .render();
        }));
        float mouseX = InputManager.get().mouseX() / Graphics.windowWidth();
        float mouseY = InputManager.get().mouseY() / Graphics.windowHeight();
        ECB.<HoverTextureItemComponent>get(HoverTextureItemComponent.class, (item, hoverTextureItem) -> item.get(GuiItemComponent.class, (GuiItemComponent guiItem) -> {
            hoverTextureItem.hovering = guiItem.x1 < mouseX && mouseX < guiItem.x2 && guiItem.y1 < mouseY && mouseY < guiItem.y2;
            textureItemShader.setUniformTexture("TEXTURE_SAMPLER", hoverTextureItem.hovering? hoverTextureItem.hoverTexture : hoverTextureItem.normalTexture);
            textureItemArray.clear()
                    .vertex( guiItem.x1, guiItem.y1,   0, 1 ) // [0]-----[1]
                    .vertex( guiItem.x2, guiItem.y1,   1, 1 ) //  |\___013|
                    .vertex( guiItem.x1, guiItem.y2,   0, 0 ) //  |023 \__|
                    .vertex( guiItem.x2, guiItem.y2,   1, 0 ) // [2]-----[3]
                    .fragment( 0, 2, 3 )
                    .fragment( 0, 1, 3 )
                    .upload()
                    .render();
        }));
    }

}
