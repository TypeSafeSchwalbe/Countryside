package devtaube.countryside.menu.components;

import rosequartz.ecb.Component;
import rosequartz.files.Resource;
import rosequartz.gfx.Texture;

import java.util.HashMap;

public class TextureItemComponent implements Component {

    static final HashMap<String, Texture> textures = new HashMap<>();

    public Texture texture;

    public TextureItemComponent(String textureName) {
        if(textures.containsKey(textureName))
            texture = textures.get(textureName);
        else {
            texture = new Texture(new Resource(textureName).forget());
            textures.put(textureName, texture);
        }
    }

}
