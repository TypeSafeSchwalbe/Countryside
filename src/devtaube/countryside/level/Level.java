package devtaube.countryside.level;

import devtaube.countryside.level.components.*;
import devtaube.countryside.level.entities.*;
import devtaube.countryside.level.pipelines.*;
import devtaube.countryside.menu.Credits;
import devtaube.countryside.menu.Menu;
import rosequartz.ConsoleManager;
import rosequartz.RoseQuartz;
import rosequartz.ecb.ECB;
import rosequartz.ecb.LogFpsPipeline;
import rosequartz.ecb.Pipeline;
import rosequartz.input.Gamepad;
import rosequartz.input.InputManager;
import rosequartz.input.Key;
import rosequartz.input.MouseButton;
import rosequartz.rng.RandomNumberGenerator;


public class Level {

    public static boolean inputPrimary = false;
    private static boolean inputPrimaryLastFrame = true;

    public static void start() {
        pipelines();
        entities();

        ConsoleManager.get().addFunction("speed", parameters -> {
            if(parameters.length < 1) return "usage: \"speed <units/second>\"";
            ECB.<PlayerMovementComponent>get(PlayerMovementComponent.class, (player, playerMovementComponent) -> playerMovementComponent.speed = Float.parseFloat(parameters[0]));
            return "Set player speed to " + parameters[0];
        });

        ConsoleManager.get().addFunction("pos", parameters -> {
            if(parameters.length < 3) return "usage: \"pos <x> <y> <z>\"";
            ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> {
                if(!parameters[0].equals("_")) player.<PositionComponent>get(PositionComponent.class).x = Float.parseFloat(parameters[0]);
                if(!parameters[1].equals("_")) player.<PositionComponent>get(PositionComponent.class).y = Float.parseFloat(parameters[1]);
                if(!parameters[2].equals("_")) player.<PositionComponent>get(PositionComponent.class).z = Float.parseFloat(parameters[2]);
            });
            return "Set player position to " + parameters[0] + ", " + parameters[1] + ", " + parameters[2];
        });
    }

    private static void pipelines() {
        ECB.clearPipelines();

        ECB.add(new LogFpsPipeline());

        ECB.add(new Pipeline(() -> {
            boolean inputPrimaryThisFrame = InputManager.get().key(Key.E) ||
                    InputManager.get().key(Key.F) ||
                    InputManager.get().mouseButton(MouseButton.LEFT) ||
                    InputManager.get().mouseButton(MouseButton.RIGHT) ||
                    InputManager.get().mainGamepad().getButton(Gamepad.Button.A) ||
                    InputManager.get().mainGamepad().getButton(Gamepad.Button.B);
            inputPrimary = !inputPrimaryLastFrame && inputPrimaryThisFrame;
            inputPrimaryLastFrame = inputPrimaryThisFrame;
        }));

        ECB.add(
                new AmbiencePipeline(),
                new RadioSoundPipeline(),
                new TileManagePipeline(),
                new EnemyPipeline(),
                new DeathTriggerPipeline(),
                new ColliderConfigurationPipeline(),
                new PlayerControllerPipeline(),
                new MatrixConfigurationPipeline(),
                new PickupPipeline(),
                new LowResRender.LowResPreparePipeline(),
                new ModelRenderPipeline(),
                new FadeOutPipeline(),
                new RadioStationPipeline(),
                new LowResRender.LowResRenderPipeline(),
                new ClockRenderPipeline(),
                new PlayerTouchInputPipeline(),
                new DeathPipeline(),
                new NoteRenderPipeline(),
                new PromptRenderPipeline()
        );
    }

    private static void entities() {
        ECB.clearEntities();
        ECB.add(new Player(0, 0, 0));
        ECB.add(new PlayerCamera());
    }

    public static void death() {
        boolean[] loadedState = { false };
        ECB.get(HasClockComponent.class, (player, hasClockComponent) -> {
            if(!hasClockComponent.getStateSaved()) return;
            hasClockComponent.loadState();
            loadedState[0] = true;
        });
        if(loadedState[0]) {
            ECB.get(PlayerComponent.class, (player, playerComponent) -> {
                player.remove(DeathTriggerComponent.class);
                player.remove(DeathComponent.class);
                player.get(PlayerRotationComponent.class, playerRotationComponent -> playerRotationComponent.enabled = true);
                player.get(PlayerMovementComponent.class, playerMovementComponent -> playerMovementComponent.enabled = true);
                player.add(new ClockRenderComponent());
            });
        } else Menu.start();
    }

}
