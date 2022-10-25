package devtaube.countryside.menu.components;

import rosequartz.ecb.Component;
import rosequartz.files.Resource;
import rosequartz.gfx.Texture;

public class HoverTextureItemComponent implements Component {

    public boolean hovering;

    public Texture normalTexture;
    public Texture hoverTexture;

    public HoverTextureItemComponent(String normalTextureName, String hoverTextureName) {
        if(TextureItemComponent.textures.containsKey(normalTextureName))
            normalTexture = TextureItemComponent.textures.get(normalTextureName);
        else {
            normalTexture = new Texture(new Resource(normalTextureName).forget());
            TextureItemComponent.textures.put(normalTextureName, normalTexture);
        }
        if(TextureItemComponent.textures.containsKey(hoverTextureName))
            hoverTexture = TextureItemComponent.textures.get(hoverTextureName);
        else {
            hoverTexture = new Texture(new Resource(hoverTextureName).forget());
            TextureItemComponent.textures.put(hoverTextureName, hoverTexture);
        }
    }

}
