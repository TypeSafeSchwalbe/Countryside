package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.*;
import devtaube.countryside.level.entities.PassiveEnemy;
import devtaube.countryside.level.entities.Tile;
import devtaube.countryside.menu.Menu;
import rosequartz.afx.Audio;
import rosequartz.afx.AudioSource;
import rosequartz.ecb.ECB;
import rosequartz.files.Resource;
import rosequartz.gfx.*;
import rosequartz.rng.RandomNumberGenerator;

import static rosequartz.RoseQuartz.*;

public class RadioStationPipeline extends GraphicsPipeline {

    public final float FINALE_DISTANCE = Tile.TILE_SIZE / 4;
    public final int PASSIVE_ENEMY_COUNT = 50;

    public final float PASSIVE_ENEMY_DISTANCE = Tile.TILE_SIZE / 4;

    private static final ShaderProgram BLACK_SHADER = new ShaderProgram(new Resource("shaders/fill_black_vertex.glsl"), new Resource("shaders/fill_black_fragment.glsl"));
    private static final VertexArray BLACK_ARRAY = new VertexArray(2)
            .vertex( -1, -1 ) // [2]-----[3]
            .vertex(  1, -1 ) //  |\___132|
            .vertex( -1,  1 ) //  |012 \__|
            .vertex(  1,  1 ) // [0]-----[1]
            .fragment( 0, 1, 2 )
            .fragment( 1, 3, 2 )
            .upload();

    private static final Audio ENEMY_SCREAM = new Audio(new Resource("sounds/enemy_scream.ogg"));
    private static final AudioSource ENEMY_SCREAM_SOURCE = new AudioSource();

    private static final Texture FINAL_TEXTURE = new Texture(new Resource("menu/final.png"));
    private static final ShaderProgram FINAL_SHADER = new ShaderProgram(new Resource("shaders/texture_item_vertex.glsl"), new Resource("shaders/texture_item_fragment.glsl"))
            .setUniformMatrix4("PROJECTION_VIEW_MATRIX", new OrthographicCamera(new CameraConfiguration().setPosition(0, 0, 1)).setBounds(0, 1, 0, 1).getProjectionViewMatrix())
            .setUniformTexture("TEXTURE_SAMPLER", FINAL_TEXTURE);

    private final VertexArray finalArray = new VertexArray(2, 2);

    public RadioStationPipeline() {
        // if distance is less than x, spawn passive enemies, disable movement and add PlayerInFinaleComponent
        add(() -> ECB.<HasRadioComponent>get(HasRadioComponent.class, (player, hasRadioComponent) -> {
            if(player.has(PlayerInFinaleComponent.class)) {
                ECB.<EnemyRotationComponent>get(EnemyRotationComponent.class, (enemy, ignored) -> ECB.remove(enemy));
                return;
            }
            PositionComponent playerPosition = player.get(PositionComponent.class);
            float playerTileX = playerPosition.x / Tile.TILE_SIZE;
            float playerTileZ = playerPosition.z / Tile.TILE_SIZE;
            float radioStationDistance = RadioSoundPipeline.getRadioStationDistance(RadioSoundPipeline.getClosestRadioStationIndex(playerTileX, playerTileZ), playerTileX, playerTileZ) * Tile.TILE_SIZE;
            if(radioStationDistance < FINALE_DISTANCE) {
                for(int enemyIndex = 0; enemyIndex < PASSIVE_ENEMY_COUNT; enemyIndex++) {
                    float enemyX = playerPosition.x + (RandomNumberGenerator.getFloat(0, 1) * 2 - 1) * PASSIVE_ENEMY_DISTANCE;
                    float enemyZ = playerPosition.z + (RandomNumberGenerator.getFloat(0, 1) * 2 - 1) * PASSIVE_ENEMY_DISTANCE;
                    ECB.add(new PassiveEnemy(enemyX, 0, enemyZ));
                }
                player.remove(PlayerMovementComponent.class);
                player.remove(HasRadioComponent.class);
                player.add(new PlayerInFinaleComponent());
            }
        }));
        // do finale
        add(() -> ECB.<PlayerInFinaleComponent>get(PlayerInFinaleComponent.class, (player, playerInFinaleComponent) -> {
            ECB.<NoteRenderComponent>get(NoteRenderComponent.class, (groundNote, ignored) -> ECB.remove(groundNote));
            if(playerInFinaleComponent.time == 0) {
                ENEMY_SCREAM_SOURCE.play(ENEMY_SCREAM);
                AmbiencePipeline.hostility = 1;
            }
            playerInFinaleComponent.time += deltaTime();
            if(playerInFinaleComponent.time > 5 && AmbiencePipeline.hostility > 0.5) AmbiencePipeline.hostility -= deltaTime() / 3 / 2;
            DepthTestingManager.get().setEnabled(false);
            BLACK_SHADER.setUniformFloat("ALPHA", Math.min(Math.max(playerInFinaleComponent.time - 5, 0) / 3, 1)) // fade out (after 5 seconds) for 3 seconds
                    .select();
            BLACK_ARRAY.render();
            if(playerInFinaleComponent.time < 8) return;
            FINAL_SHADER.select();
            float height = 0.2f;
            float width = height * FINAL_TEXTURE.getWidth() / FINAL_TEXTURE.getHeight() * Graphics.windowHeight() / Graphics.windowWidth();
            float x = (1 - width) / 2;
            float y = (1 - height) / 2;
            finalArray.clear()
                    .vertex( x,         y,            0, 0 ) // [2]-----[3]
                    .vertex( x + width, y,            1, 0 ) //  |\___132|
                    .vertex( x,         y + height,   0, 1 ) //  |012 \__|
                    .vertex( x + width, y + height,   1, 1 ) // [0]-----[1]
                    .fragment( 0, 1, 2 )
                    .fragment( 1, 3, 2 )
                    .upload()
                    .render();
            if(playerInFinaleComponent.time > 23) Menu.start(); // continue after 8 + 15 (cooldown) seconds
        }));
    }

}
