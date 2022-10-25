package devtaube.countryside.menu;

import devtaube.countryside.level.Level;
import devtaube.countryside.level.TileGenerator;
import devtaube.countryside.level.entities.Tile;
import devtaube.countryside.level.pipelines.*;
import devtaube.countryside.menu.components.ClickableItemComponent;
import devtaube.countryside.menu.components.GuiItemComponent;
import devtaube.countryside.menu.components.HoverTextureItemComponent;
import devtaube.countryside.menu.components.TextureItemComponent;
import devtaube.countryside.menu.entities.MenuPlayer;
import devtaube.countryside.menu.entities.MenuPlayerCamera;
import devtaube.countryside.menu.pipelines.ItemPipeline;
import devtaube.countryside.menu.pipelines.MouseSpeedRenderPipeline;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Entity;
import rosequartz.ecb.LogFpsPipeline;
import rosequartz.files.FileManager;
import rosequartz.input.InputManager;

public class Menu {

    public static final String MOUSE_SENSITIVITY_FILE = "mouse_sensitivity";

    public static void start() {
        InputManager.get().setMouseVisible(true);
        readMouseSensitivity();
        pipelines();
        entities();
        gui();
    }

    private static void readMouseSensitivity() {
        if(!FileManager.get().fileExists(MOUSE_SENSITIVITY_FILE)) FileManager.get().writeFileString(MOUSE_SENSITIVITY_FILE, "1");
        PlayerRotationBehavior.SPEED = Float.parseFloat(FileManager.get().readFileString(MOUSE_SENSITIVITY_FILE));
    }

    public static void pipelines() {
        ECB.clearPipelines();

        ECB.add(new LogFpsPipeline());

        ECB.add(
                new AmbiencePipeline(),
                new RadioSoundPipeline(),
                new MatrixConfigurationPipeline(),
                new LowResRender.LowResPreparePipeline(),
                new ModelRenderPipeline(),
                new LowResRender.LowResRenderPipeline()
        );

        ECB.add(new ItemPipeline());
    }

    public static void entities() {
        ECB.clearEntities();
        ECB.add(new MenuPlayer(-Tile.TILE_SIZE, Tile.TILE_SIZE / 3, 0, 0, 0, -50));
        ECB.add(new MenuPlayerCamera());
        TileGenerator.generateTile(-1, 1);
        TileGenerator.generateTile(-1, 0);
        TileGenerator.generateTile(-1, -1);
        TileGenerator.generateTile(-1, -2);
    }

    private static void gui() {
        ECB.add(new MouseSpeedRenderPipeline());

        ECB.add(new Entity().add(
                new GuiItemComponent().size(0, 0.25).sizeRelativeX(2.5).position(0.5, 0.25).translate(-0.5, -0.5),
                new TextureItemComponent("menu/logo.png")
        ));

        ECB.add(new Entity().add(
                new GuiItemComponent().size(0, 0.1).sizeRelativeX(2.25).position(0.5, 0.7).translate(-0.5, -0.5),
                new HoverTextureItemComponent("menu/button_play.png", "menu/button_play_hover.png"),
                new ClickableItemComponent(Level::start)
        ));
        ECB.add(new Entity().add(
                new GuiItemComponent().size(0, 0.1).sizeRelativeX(3.75).position(0.5, 0.85).translate(-0.5, -0.5),
                new HoverTextureItemComponent("menu/button_credits.png", "menu/button_credits_hover.png"),
                new ClickableItemComponent(Credits::start)
        ));

        ECB.add(new Entity().add(
                new GuiItemComponent().size(0, 0.05).sizeRelativeX(0.91).position(0.025, 0.91).translate(0, -1),
                new HoverTextureItemComponent("menu/button_add.png", "menu/button_add_hover.png"),
                new ClickableItemComponent(() -> {
                    PlayerRotationBehavior.SPEED += 0.1f;
                    PlayerRotationBehavior.SPEED = Math.min(Math.max(PlayerRotationBehavior.SPEED, 0.1f), 5);
                    FileManager.get().writeFileString(MOUSE_SENSITIVITY_FILE, String.valueOf(PlayerRotationBehavior.SPEED));
                })
        ));
        ECB.add(new Entity().add(
                new GuiItemComponent().size(0, 0.05).sizeRelativeX(0.91).position(0.025, 0.975).translate(0, -1),
                new HoverTextureItemComponent("menu/button_subtract.png", "menu/button_subtract_hover.png"),
                new ClickableItemComponent(() -> {
                    PlayerRotationBehavior.SPEED -= 0.1f;
                    PlayerRotationBehavior.SPEED = Math.min(Math.max(PlayerRotationBehavior.SPEED, 0.1f), 5);
                    FileManager.get().writeFileString(MOUSE_SENSITIVITY_FILE, String.valueOf(PlayerRotationBehavior.SPEED));
                })
        ));
    }

}
