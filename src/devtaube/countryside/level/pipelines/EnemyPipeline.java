package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.*;
import devtaube.countryside.level.entities.Enemy;
import devtaube.countryside.level.entities.Tile;
import rosequartz.RoseQuartz;
import rosequartz.ecb.ECB;
import rosequartz.gfx.GraphicsPipeline;
import rosequartz.rng.RandomNumberGenerator;

import static rosequartz.RoseQuartz.deltaTime;

public class EnemyPipeline extends GraphicsPipeline {

    private static long START_TIME;
    public static final long ENEMY_SPAWN_COOLDOWN = 1000 * 60 * 10;
    public static final float ENEMY_SPAWN_RADIUS = Tile.TILE_SIZE * 5;

    public static final float ENEMY_SMALL_TELEPORT_CHANCE = 0.0333f; // chance of the enemy teleporting a little at sight / second, 0 = 0%, 1 = 100% >> 0.0333 = ca every 30s
    public static final float ENEMY_LARGE_TELEPORT_CHANCE = 0.0055f; // chance of the enemy teleporting away at sight / second,  0 = 0%, 1 = 100% >> 0.0083 = ca every two minutes

    public static final float ENEMY_TELEPORT_RADIUS = Tile.TILE_SIZE * 5;

    private final float ANGLE_VECTOR_X = 1;
    private final float ANGLE_VECTOR_Z = 0;

    public static final float HOSTILITY_DISTANCE = Tile.TILE_SIZE * 3; // distance where hostility == 0

    public static final float MINIMUM_MOVEMENT_ANGLE = 60; // enemy may only move if angle between them is more than this

    public static final float FATAL_DISTANCE = 3;
    public static final float DEATH_TRIGGER_POSITION_DISTANCE = 3;

    private boolean doSave = true;

    public EnemyPipeline() {
        START_TIME = System.currentTimeMillis();
        // spawn enemy if 5 Minutes went by and no enemy has been spawned
        add(() -> {
            if(START_TIME + ENEMY_SPAWN_COOLDOWN > System.currentTimeMillis()) return;
            boolean[] enemyFound = { false };
            ECB.get(EnemyMovementComponent.class, (enemy, enemyMovementComponent) -> enemyFound[0] = true );
            if(enemyFound[0]) return;
            float enemySpawnAngle = RandomNumberGenerator.getFloat(0, 360);
            ECB.add(new Enemy((float) Math.cos(Math.toRadians(enemySpawnAngle)) * ENEMY_SPAWN_RADIUS, 0, (float) Math.sin(Math.toRadians(enemySpawnAngle)) * ENEMY_SPAWN_RADIUS));
        });
        // face player
        add(() -> ECB.get(PlayerComponent.class, (player, playerComponent) -> {
            PositionComponent playerPosition = player.get(PositionComponent.class);
            ECB.get(EnemyRotationComponent.class, (enemy, enemyRotationComponent) -> {
                PositionComponent enemyPosition = enemy.get(PositionComponent.class);
                enemy.get(RotationComponent.class, (RotationComponent rotationComponent) -> {
                    float distanceX = playerPosition.x - enemyPosition.x;
                    float distanceZ = playerPosition.z - enemyPosition.z;
                    float distanceL = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2));
                    distanceX /= distanceL;
                    distanceZ /= distanceL;
                    float den = (float) (Math.sqrt(Math.pow(ANGLE_VECTOR_X, 2) + Math.pow(ANGLE_VECTOR_Z, 2)) * Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2)));
                    float cos = (ANGLE_VECTOR_X * distanceX + ANGLE_VECTOR_Z * distanceZ) / den;
                    float angle = (float) Math.toDegrees(Math.acos(cos));
                    if(distanceZ > 0) angle = 360 - angle;
                    rotationComponent.y = angle;
                });
            });
        }));
        // configure hostility
        add(() -> ECB.get(PlayerComponent.class, (player, playerComponent) -> {
            if(player.has(PlayerInFinaleComponent.class)) return;
            if(player.has(DeathComponent.class)) return;
            PositionComponent playerPosition = player.get(PositionComponent.class);
            float[] maxHostility = { 0 };
            ECB.<EnemyRotationComponent>get(EnemyRotationComponent.class, (enemy, enemyRotationComponent) -> {
                PositionComponent enemyPosition = enemy.get(PositionComponent.class);
                float distanceX = playerPosition.x - enemyPosition.x;
                float distanceZ = playerPosition.z - enemyPosition.z;
                float distanceL = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2));
                maxHostility[0] = Math.max(1 - distanceL / HOSTILITY_DISTANCE, maxHostility[0]);
            });
            AmbiencePipeline.hostility = maxHostility[0];
        }));
        // check if enemy may move and move if it may
        add(() -> ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> {
            PositionComponent playerPosition = player.get(PositionComponent.class);
            RotationComponent playerRotation = player.get(RotationComponent.class);
            float[] playerLook = { (float) Math.cos(Math.toRadians(playerRotation.y)), (float) Math.sin(Math.toRadians(playerRotation.y)) };
            ECB.<EnemyMovementComponent>get(EnemyMovementComponent.class, (enemy, enemyMovementComponent) -> {
                PositionComponent enemyPosition = enemy.get(PositionComponent.class);
                EnemyVisibilityBlockComponent enemyVisibilityBlockComponent = enemy.get(EnemyVisibilityBlockComponent.class);
                float distanceX = enemyPosition.x - playerPosition.x;
                float distanceZ = enemyPosition.z - playerPosition.z;
                float distanceL = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2));
                distanceX /= distanceL;
                distanceZ /= distanceL;
                float den = (float) (Math.sqrt(Math.pow(playerLook[0], 2) + Math.pow(playerLook[1], 2)) * Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2)));
                float cos = (playerLook[0] * distanceX + playerLook[1] * distanceZ) / den;
                float angle = (float) Math.toDegrees(Math.acos(cos));
                boolean mayMove = angle > MINIMUM_MOVEMENT_ANGLE;
                if(!mayMove) {
                    if(distanceL < EnemyVisibilityBlockComponent.MINIMUM_SEEN_DISTANCE) enemyVisibilityBlockComponent.seenByPlayer = true;
                    if(RandomNumberGenerator.getFloat(0, 1) < ENEMY_SMALL_TELEPORT_CHANCE * deltaTime()) { // small teleport
                        enemy.get(TeleportSoundComponent.class, (TeleportSoundComponent teleportSoundComponent) -> teleportSoundComponent.play(enemyPosition.x, enemyPosition.y, enemyPosition.z));
                        enemyPosition.x = playerPosition.x - (enemyPosition.x - playerPosition.x);
                        enemyPosition.z = playerPosition.z - (enemyPosition.z - playerPosition.z);
                    }
                    if(RandomNumberGenerator.getFloat(0, 1) < ENEMY_LARGE_TELEPORT_CHANCE * deltaTime()) { // large teleport
                        enemy.get(TeleportSoundComponent.class, (TeleportSoundComponent teleportSoundComponent) -> teleportSoundComponent.play(enemyPosition.x, enemyPosition.y, enemyPosition.z));
                        float teleportAngle = RandomNumberGenerator.getFloat(0, 360);
                        enemyPosition.x = playerPosition.x + (float) Math.cos(Math.toRadians(teleportAngle)) * ENEMY_TELEPORT_RADIUS;
                        enemyPosition.z = playerPosition.z + (float) Math.sin(Math.toRadians(teleportAngle)) * ENEMY_TELEPORT_RADIUS;
                        enemyVisibilityBlockComponent.seenByPlayer = false;
                    }
                    return;
                }
                if(distanceL > EnemyVisibilityBlockComponent.BLOCKED_DISTANCE || enemyVisibilityBlockComponent.seenByPlayer) {
                    enemyPosition.x -= distanceX * enemyMovementComponent.speed * deltaTime();
                    enemyPosition.z -= distanceZ * enemyMovementComponent.speed * deltaTime();
                }
                if(distanceL <= HOSTILITY_DISTANCE / 2 && doSave) {
                    player.get(HasClockComponent.class, (HasClockComponent hasClockComponent) -> {
                        hasClockComponent.saveState();
                    });
                    doSave = false;
                }
                if(distanceL > HOSTILITY_DISTANCE / 2) doSave = true;
            });
        }));
        // if distance to player is lower than the death distance, delete the enemy from the ecb and add a death trigger to the player.
        add(() -> ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> {
            PositionComponent playerPosition = player.get(PositionComponent.class);
            RotationComponent playerRotation = player.get(RotationComponent.class);
            ECB.<EnemyMovementComponent>get(EnemyMovementComponent.class, (enemy, enemyMovementComponent) -> {
                PositionComponent enemyPosition = enemy.get(PositionComponent.class);
                float distanceX = enemyPosition.x - playerPosition.x;
                float distanceZ = enemyPosition.z - playerPosition.z;
                float distanceL = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceZ, 2));
                distanceX /= distanceL;
                distanceZ /= distanceL;
                if(distanceL < FATAL_DISTANCE) {
                    player.add(new DeathTriggerComponent(
                            (playerRotation.y + 180) % 360,
                            playerPosition.x - distanceX * DEATH_TRIGGER_POSITION_DISTANCE,
                            playerPosition.z - distanceZ * DEATH_TRIGGER_POSITION_DISTANCE
                    ));
                    ECB.remove(enemy);
                }
            });
        }));
    }

}
