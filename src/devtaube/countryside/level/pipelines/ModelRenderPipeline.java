package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.*;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Entity;
import rosequartz.files.Resource;
import rosequartz.gfx.DepthTestingManager;
import rosequartz.gfx.GraphicsPipeline;
import rosequartz.gfx.ShaderProgram;

import java.util.ArrayList;
import java.util.Collections;

public class ModelRenderPipeline extends GraphicsPipeline {

    public static final int LIGHT_COUNT = 16;

    private static final ShaderProgram MODEL_RENDER_SHADER = new ShaderProgram(new Resource("shaders/model_vertex.glsl").forget(), new Resource("shaders/model_fragment.glsl").forget())
            .setUniformFloat("AMBIENT_STRENGTH", 0.25f);
    private boolean depthTesting = false;
    private boolean useAmbientLight = true;

    public ModelRenderPipeline() {
        add(() -> {
            ECB.<PlayerCameraComponent>get(PlayerCameraComponent.class, (camera, playerCameraComponent) -> MODEL_RENDER_SHADER.setUniformMatrix4("PROJECTION_VIEW_MATRIX", camera.<PlayerCameraComponent>get(PlayerCameraComponent.class).camera.getProjectionViewMatrix()));
            MODEL_RENDER_SHADER.select();
            ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> player.get(PositionComponent.class, (PositionComponent playerPosition) -> {
                ArrayList<float[]> lights = new ArrayList<>(); // float[ x, y, z, strength, distance ] * n
                ECB.<LightComponent>get(LightComponent.class, (light, lightComponent) -> light.get(PositionComponent.class, (PositionComponent lightPosition) -> {
                    float distanceX = playerPosition.x - lightPosition.x;
                    float distanceY = playerPosition.y - lightPosition.y;
                    float distanceZ = playerPosition.z - lightPosition.z;
                    float distance = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2) + Math.pow(distanceZ, 2));
                    lights.add(new float[] { lightPosition.x, lightPosition.y, lightPosition.z, light.<LightComponent>get(LightComponent.class).strength, distance });
                }));
                Collections.sort(lights, (o1, o2) -> Integer.compare((int) (o1[4] * 1000), (int) (o2[4] * 1000))); // DO NOT CHANGE THIS LINE!
                float[][] lightPositions = new float[LIGHT_COUNT][3];
                float[] lightStrengths = new float[LIGHT_COUNT];
                for(int lightIndex = 0; lightIndex < LIGHT_COUNT; lightIndex++) {
                    if(lightIndex >= lights.size()) {
                        lightPositions[lightIndex] = new float[] { 0, 0, 0 };
                        lightStrengths[lightIndex] = 0;
                        continue;
                    }
                    float[] lightArray = lights.get(lightIndex);
                    lightPositions[lightIndex] = new float[] { lightArray[0], lightArray[1], lightArray[2] };
                    lightStrengths[lightIndex] = lightArray[3];
                }
                MODEL_RENDER_SHADER.setUniformVec("LIGHT_POSITIONS", lightPositions, 16)
                        .setUniformFloat("LIGHT_STRENGTHS", lightStrengths, 16);
            }));
            DepthTestingManager.get().setEnabled(depthTesting);
            ECB.get(ModelComponent.class, (Entity entity, ModelComponent modelComponent) -> {
                if(depthTesting != modelComponent.depthTesting) {
                    DepthTestingManager.get().setEnabled(modelComponent.depthTesting);
                    depthTesting = modelComponent.depthTesting;
                }
                if(useAmbientLight != modelComponent.useAmbientLight) {
                    MODEL_RENDER_SHADER.setUniformFloat("AMBIENT_STRENGTH", (modelComponent.useAmbientLight? 0.25f : 1));
                    useAmbientLight = modelComponent.useAmbientLight;
                }
                MODEL_RENDER_SHADER.setUniformMatrix4("MODEL_MATRIX", modelComponent.modelInstance.getModelMatrix())
                        .setUniformMatrix4("MODEL_ROTATION_MATRIX", modelComponent.modelInstance.getModelRotationMatrix())
                        .setUniformTexture("NORMAL_TEXTURE_SAMPLER", modelComponent.normalTexture)
                        .setUniformTexture("BRIGHT_TEXTURE_SAMPLER", modelComponent.brightTexture);
                modelComponent.model.getVertexArray().render();
            });
        });
    }

}
