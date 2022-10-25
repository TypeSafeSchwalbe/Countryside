package devtaube.countryside.level.components;

import rosequartz.ecb.Component;
import rosequartz.files.Resource;
import rosequartz.gfx.*;

import java.util.HashMap;

public class ModelComponent implements Component  {

    private static final HashMap<String, Model> models = new HashMap<>();
    private static final HashMap<String, Texture> textures = new HashMap<>();

    public Model model;
    public Texture normalTexture;
    public Texture brightTexture;
    public ModelInstance modelInstance;
    public boolean depthTesting = true;
    public boolean useAmbientLight = true;

    public ModelComponent(String modelName, String normalTextureName, String brightTextureName) {
        if(models.containsKey(modelName))
            model = models.get(modelName);
        else {
            model = new Model(new Resource(modelName).forget(), new VertexBuilder<>(Model.VertexStructure.VERTEX_POSITION, Model.VertexStructure.NORMAL_VECTOR, Model.VertexStructure.TEXTURE_MAPPING));
            models.put(modelName, model);
        }
        if(textures.containsKey(normalTextureName))
            normalTexture = textures.get(normalTextureName);
        else {
            normalTexture = new Texture(new Resource(normalTextureName).forget());
            textures.put(normalTextureName, normalTexture);
        }
        if(textures.containsKey(brightTextureName))
            brightTexture = textures.get(brightTextureName);
        else {
            brightTexture = new Texture(new Resource(brightTextureName).forget());
            textures.put(brightTextureName, brightTexture);
        }
        modelInstance = new ModelInstance();
    }

    public ModelComponent(VertexArray vertexArray, String normalTextureName, String brightTextureName) {
        model = new Model(vertexArray, new VertexBuilder<>(Model.VertexStructure.VERTEX_POSITION, Model.VertexStructure.NORMAL_VECTOR, Model.VertexStructure.TEXTURE_MAPPING));
        if(textures.containsKey(normalTextureName))
            normalTexture = textures.get(normalTextureName);
        else {
            normalTexture = new Texture(new Resource(normalTextureName).forget());
            textures.put(normalTextureName, normalTexture);
        }
        if(textures.containsKey(brightTextureName))
            brightTexture = textures.get(brightTextureName);
        else {
            brightTexture = new Texture(new Resource(brightTextureName).forget());
            textures.put(brightTextureName, brightTexture);
        }
        modelInstance = new ModelInstance();
    }

}
