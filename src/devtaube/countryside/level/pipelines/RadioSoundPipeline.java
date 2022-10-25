package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.components.HasRadioComponent;
import devtaube.countryside.level.components.PlayerComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.entities.Tile;
import rosequartz.afx.Audio;
import rosequartz.afx.AudioSource;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Pipeline;
import rosequartz.files.Resource;
import rosequartz.rng.RandomNumberGenerator;

public class RadioSoundPipeline extends Pipeline {

    private static final Audio RADIO = new Audio(new Resource("sounds/radio.ogg"));
    private static final Audio INTERMISSION = new Audio(new Resource("sounds/intermission.ogg"));

    private static AudioSource radioSource;
    private static final AudioSource intermissionSource = new AudioSource();

    private static long lastIntermissionTime = System.currentTimeMillis();
    private static final long INTERMISSION_COOLDOWN = 1000 * 60;
    private static final int INTERMISSION_FULL_LENGTH = 5000;

    public static final float MINIMUM_RADIO_STATION_DISTANCE = 4;
    public static final float MAXIMUM_RADIO_STATION_DISTANCE = 50;
    public static final int RADIO_STATION_TILE_COUNT = 16; // radio station should appear every x tiles (256 = 16 * 16 tiles)

    public static final float LOWEST_QUALITY_DISTANCE = 32; // maximum amount of tiles for intermissions to be heard

    public static final int[][] RADIO_STATIONS = generateRadioStationPositions();

    private static int[][] generateRadioStationPositions() {
        float tiles_in_radio_station_distance = (float) (2 * MAXIMUM_RADIO_STATION_DISTANCE * Math.PI);
        int radio_station_count = (int) Math.ceil(tiles_in_radio_station_distance / RADIO_STATION_TILE_COUNT);
        int[][] result = new int[radio_station_count][2];
        for(int stationIndex = 0; stationIndex < result.length; stationIndex++) {
            float stationDistance = (float) (RandomNumberGenerator.getFloat(0, 1) * (MAXIMUM_RADIO_STATION_DISTANCE - MINIMUM_RADIO_STATION_DISTANCE)) + MINIMUM_RADIO_STATION_DISTANCE;
            float stationAngle = RandomNumberGenerator.getFloat(0, 360);
            result[stationIndex][0] = (int) (Math.cos(Math.toRadians(stationAngle)) * stationDistance);
            result[stationIndex][1] = (int) (Math.sin(Math.toRadians(stationAngle)) * stationDistance);
        }
        return result;
    }

    public static float getRadioStationDistance(int station, float tileX, float tileZ) {
        return (float) Math.sqrt(Math.pow(tileX - RADIO_STATIONS[station][0] - 0.5f, 2) + Math.pow(tileZ - RADIO_STATIONS[station][1] - 0.5f, 2));
    }

    public static int getClosestRadioStationIndex(float tileX, float tileZ) {
        int closestRadioStationIndex = 0;
        float closestDistance = MAXIMUM_RADIO_STATION_DISTANCE;
        for(int radioStationIndex = 0; radioStationIndex < RADIO_STATIONS.length; radioStationIndex++) {
            float distance = getRadioStationDistance(radioStationIndex, tileX, tileZ);
            if(distance >= closestDistance) continue;
            closestDistance = distance;
            closestRadioStationIndex = radioStationIndex;
        }
        return closestRadioStationIndex;
    }

    public RadioSoundPipeline() {
        if(radioSource == null) radioSource = new AudioSource().setVolume(0);
        add(() -> {
            boolean[] playerExists = { false };
            ECB.<PlayerComponent>get(PlayerComponent.class, (player, ignored) -> {
                boolean hasRadio = player.has(HasRadioComponent.class);
                if(hasRadio) playerExists[0] = true;
                if(hasRadio) intermissionSource.setVolume(0);
                player.<HasRadioComponent>get(HasRadioComponent.class, hasRadioComponent -> {
                    PositionComponent playerPosition = player.get(PositionComponent.class);
                    float playerTileX = playerPosition.x / Tile.TILE_SIZE;
                    float playerTileZ = playerPosition.z / Tile.TILE_SIZE;
                    float radioStationDistance = getRadioStationDistance(getClosestRadioStationIndex(playerTileX, playerTileZ), playerTileX, playerTileZ);
                    float intermissionQuality = Math.max(1 - (radioStationDistance / LOWEST_QUALITY_DISTANCE), 0) * (1 - Math.max(AmbiencePipeline.hostility * 2, 1));
                    boolean intermissionAudible = (lastIntermissionTime + (long) ((float) INTERMISSION_FULL_LENGTH * intermissionQuality)) > System.currentTimeMillis();
                    radioSource.setVolume(hasRadio ? (1 - intermissionQuality / 2) * (1 - Math.max(AmbiencePipeline.hostility * 2, 1)) : 0);
                    intermissionSource.setVolume(hasRadio && intermissionAudible? intermissionQuality : 0);
                    if(lastIntermissionTime + INTERMISSION_COOLDOWN < System.currentTimeMillis()) {
                        lastIntermissionTime = System.currentTimeMillis();
                        radioSource.play(RADIO);
                        intermissionSource.play(INTERMISSION);
                    }
                });
            });
            if(!playerExists[0]) {
                radioSource.setVolume(0);
                intermissionSource.setVolume(0);
            }
        });
    }

}
