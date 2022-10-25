package devtaube.countryside.level;

import devtaube.countryside.level.components.ModelComponent;
import devtaube.countryside.level.entities.*;
import devtaube.countryside.level.pipelines.RadioSoundPipeline;
import rosequartz.coll.SphereCollider;
import rosequartz.ecb.ECB;
import rosequartz.files.Resource;
import rosequartz.gfx.VertexArray;
import rosequartz.rng.NoiseGenerator;
import rosequartz.rng.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.Arrays;

public class TileGenerator {

    private static final NoiseGenerator NOISE_GENERATOR = new NoiseGenerator();

    private static final int[][][] TILE_SUB_CONTENTS = loadTileSubContents();
    private static final float[][] TILE_PERLIN_RANGES = loadTilePerlinRanges();

    private static final float TILE_PERLIN_MULTIPLIER = 11;

    public static final int TILE_VARIANT_COUNT = 12;

    private static final int GRASS_PER_SUBTILE = 50;
    private static final int CORN_PER_SUBTILE = 150;
    private static final int HOUSES_PER_SUBTILE = 1;
    private static final int LANTERNS_PER_SUBTILE = 1;
    private static final float LANTERN_SPAWN_CHANCE = 0.75f; // 0 = 0%, 1 = 100%
    private static final int TREES_PER_SUBTILE = 20;

    private static final float RADIO_SPAWN_CHANCE  = 0.1f; // 0 = 0%, 1 = 100%
    private static final float CLOCK_SPAWN_CHANCE  = 0.15f; // 0 = 0%, 1 = 100%

    private static int[][][] loadTileSubContents() {
        int[][][] result = new int[TILE_VARIANT_COUNT][9][9];
        for(int variant = 0; variant < TILE_VARIANT_COUNT; variant++) {
            String[] variantContentLines = new Resource("tiles/tile" + variant + ".txt").forget().toString().split("\\r\\n|\\n|\\r");
            int tileZ = 0;
            for(String line: variantContentLines) {
                if(line.startsWith("#") || line.startsWith("perlin") || line.trim().length() == 0) continue;
                String[] tiles = line.split(" ");
                for(int tileX = 0; tileX < tiles.length; tileX++)
                    result[variant][8 - tileZ][tileX] = Integer.parseInt(tiles[tileX]);
                tileZ++;
            }
        }
        return result;
    }

    private static float[][] loadTilePerlinRanges() {
        float[][] result = new float[TILE_VARIANT_COUNT][2];
        for(int variant = 0; variant < TILE_VARIANT_COUNT; variant++) {
            String[] variantContentLines = new Resource("tiles/tile" + variant + ".txt").forget().toString().split("\\r\\n|\\n|\\r");
            for(String line: variantContentLines) {
                if(!line.startsWith("perlin")) continue;
                String[] values = line.split(" ");
                result[variant][0] = Float.parseFloat(values[1]);
                result[variant][1] = Float.parseFloat(values[2]);
            }
        }
        return result;
    }

    private static boolean variantImpossible(int variant) {
        return Math.abs(TILE_PERLIN_RANGES[variant][0] - TILE_PERLIN_RANGES[variant][1]) < 0.01f; // difference min <-> max is less than 0.01f?
    }

    public static void generateTile(int tileX, int tileZ) {
        float perlinValue = NOISE_GENERATOR.getPerlin(tileX * TILE_PERLIN_MULTIPLIER, 0, tileZ * TILE_PERLIN_MULTIPLIER);
        int generationTriesLeft = 50; // after x tries valid perlin range is ignored
        int variant;
        do {
            do {
                variant = RandomNumberGenerator.getInt(0, TILE_VARIANT_COUNT);
            } while(variantImpossible(variant)); // re gen if variant *should* be impossible
            if(perlinValue > TILE_PERLIN_RANGES[variant][0] && perlinValue < TILE_PERLIN_RANGES[variant][1]) break;
            generationTriesLeft--;
        } while(generationTriesLeft > 0);
        boolean isRadioStation = false;
        for(int[] radioStation: RadioSoundPipeline.RADIO_STATIONS) {
            if(radioStation[0] != tileX || radioStation[1] != tileZ) continue;
            isRadioStation = true;
            break;
        }
        if(isRadioStation) variant = 11;
        ECB.add(new Tile(tileX, tileZ, variant));
        generateContents(tileX, tileZ, variant);
    }

    public static VertexArray generateTileArray() {
        return new VertexArray(3, 3, 2)
                .vertex( 0, 0, 0,   0, 1, 0,   0, 0 )
                .vertex( 1, 0, 0,   0, 1, 0,   1, 0 )
                .vertex( 0, 0, 1,   0, 1, 0,   0, 1 )
                .vertex( 1, 0, 1,   0, 1, 0,   1, 1 )
                .fragment( 0, 1, 2 )
                .fragment( 1, 3, 2 )
                .upload();
    }

    public static void generateContents(int tileLocationX, int tileLocationZ, int variant) {
        int[][] tileSubContents = Arrays.copyOf(TILE_SUB_CONTENTS[variant], TILE_SUB_CONTENTS[variant].length);
        VertexArray grassArray = new VertexArray(3, 3, 2);
        VertexArray cornArray = new VertexArray(3, 3, 2);
        VertexArray treeArray = new VertexArray(3, 3, 2);
        ArrayList<SphereCollider> treeColliders = new ArrayList<>();
        for(int tileZ = 0; tileZ < 9; tileZ++) {
            for(int tileX = 0; tileX < 9; tileX++) {
                switch(tileSubContents[tileZ][tileX]) {
                    case 1: spawnGrass(grassArray, tileX, tileZ); break;
                    case 2: spawnCorn(cornArray, tileX, tileZ); break;
                    case 3: spawnHouse(tileLocationX, tileLocationZ, tileX, tileZ); spawnGrass(grassArray, tileX, tileZ); break;
                    case 4: spawnLantern(tileLocationX, tileLocationZ, tileX, tileZ, false); break;
                    case 5: spawnLantern(tileLocationX, tileLocationZ, tileX, tileZ, true); break;
                    case 6: spawnTree(treeArray, tileLocationX, tileLocationZ, tileX, tileZ, treeColliders); spawnGrass(grassArray, tileX, tileZ); break;
                }
            }
        }
        grassArray.upload();
        cornArray.upload();
        treeArray.upload();
        ECB.add(new Grass(grassArray, tileLocationX * Tile.TILE_SIZE, tileLocationZ * Tile.TILE_SIZE, tileLocationX, tileLocationZ));
        ECB.add(new Corn(cornArray, tileLocationX * Tile.TILE_SIZE, tileLocationZ * Tile.TILE_SIZE, tileLocationX, tileLocationZ));
        ECB.add(new Tree(treeArray, tileLocationX * Tile.TILE_SIZE, tileLocationZ * Tile.TILE_SIZE, tileLocationX, tileLocationZ, treeColliders.toArray(new SphereCollider[0])));
        ECB.add(new GroundNote(tileLocationX * Tile.TILE_SIZE + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE), tileLocationZ * Tile.TILE_SIZE + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE), tileLocationX, tileLocationZ));
        if(variant == 11) {
            float positionX = tileLocationX * Tile.TILE_SIZE + Tile.TILE_SIZE / 2;
            float positionZ = tileLocationZ * Tile.TILE_SIZE + Tile.TILE_SIZE / 2;
            ECB.add(new RadioStation(positionX, positionZ, tileLocationX, tileLocationZ));
            ECB.add(new LightSource(positionX, 5, positionZ, tileLocationX, tileLocationZ, 100));
        }
        if(RandomNumberGenerator.getFloat(0, 1) < RADIO_SPAWN_CHANCE) ECB.add(new Radio(tileLocationX * Tile.TILE_SIZE + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE), tileLocationZ * Tile.TILE_SIZE + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE), tileLocationX, tileLocationZ));
        if(RandomNumberGenerator.getFloat(0, 1) < CLOCK_SPAWN_CHANCE) ECB.add(new Clock(tileLocationX * Tile.TILE_SIZE + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE), tileLocationZ * Tile.TILE_SIZE + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE), tileLocationX, tileLocationZ));
    }

    private static void spawnGrass(VertexArray grassArray, int subTileX, int subTileZ) {
        for(int grassIndex = 0; grassIndex < GRASS_PER_SUBTILE; grassIndex++) {
            float locationX = subTileX * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            float locationZ = subTileZ * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            ModelComponent grassModelComponent = new ModelComponent("models/grass.obj", "models/grass.png", "models/grass_bright.png");
            grassModelComponent.modelInstance.setPosition(locationX, 0, locationZ)
                    .rotate(0, RandomNumberGenerator.getFloat(0, 360), 0);
            grassArray.addAll(grassModelComponent.model, grassModelComponent.modelInstance);
        }
    }

    private static void spawnCorn(VertexArray cornArray, int subTileX, int subTileZ) {
        for(int cornIndex = 0; cornIndex < CORN_PER_SUBTILE; cornIndex++) {
            float locationX = subTileX * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            float locationZ = subTileZ * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            ModelComponent cornModelComponent = new ModelComponent("models/corn.obj", "models/corn.png", "models/corn_bright.png");
            cornModelComponent.modelInstance.setPosition(locationX, 0, locationZ)
                    .rotate(0, RandomNumberGenerator.getFloat(0, 360), 0);
            cornArray.addAll(cornModelComponent.model, cornModelComponent.modelInstance);
        }
    }

    private static void spawnHouse(int tileX, int tileZ, int subTileX, int subTileZ) {
        for(int houseIndex = 0; houseIndex < HOUSES_PER_SUBTILE; houseIndex++) {
            float positionX = tileX * Tile.TILE_SIZE + subTileX * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            float positionZ = tileZ * Tile.TILE_SIZE + subTileZ * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            ECB.add(new House(positionX, positionZ, tileX, tileZ));
        }
    }

    private static void spawnLantern(int tileX, int tileZ, int subTileX, int subTileZ, boolean orientation) {
        for(int lanternIndex = 0; lanternIndex < LANTERNS_PER_SUBTILE; lanternIndex++) {
            if(RandomNumberGenerator.getFloat(0, 1) > LANTERN_SPAWN_CHANCE) continue;
            float position = (RandomNumberGenerator.getFloat(0, 0.1f)) + (RandomNumberGenerator.getInt(0, 2) == 0? 0.2f : 0.7f);
            float positionX = tileX * Tile.TILE_SIZE + subTileX * Tile.TILE_SIZE / 9 + (orientation? position : RandomNumberGenerator.getFloat(0, 1)) * Tile.TILE_SIZE / 9;
            float positionZ = tileZ * Tile.TILE_SIZE + subTileZ * Tile.TILE_SIZE / 9 + (orientation? RandomNumberGenerator.getFloat(0, 1) : position) * Tile.TILE_SIZE / 9;
            ECB.add(new Lantern(positionX, positionZ, tileX, tileZ));
            ECB.add(new LightSource(positionX, 5, positionZ, tileX, tileZ, 50));
        }
    }

    private static void spawnTree(VertexArray treeArray, int tileX, int tileZ, int subTileX, int subTileZ, ArrayList<SphereCollider> treeColliders) {
        for(int treeIndex = 0; treeIndex < TREES_PER_SUBTILE; treeIndex++) {
            float locationX = subTileX * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            float locationZ = subTileZ * Tile.TILE_SIZE / 9 + RandomNumberGenerator.getFloat(0, Tile.TILE_SIZE / 9);
            ModelComponent treeModelComponent = new ModelComponent("models/tree.obj", "models/tree.png", "models/tree_bright.png");
            treeModelComponent.modelInstance.setPosition(locationX, 0, locationZ)
                    .rotate(0, RandomNumberGenerator.getFloat(0, 360), 0);
            treeArray.addAll(treeModelComponent.model, treeModelComponent.modelInstance);
            treeColliders.add(new SphereCollider(locationX + tileX * Tile.TILE_SIZE, 0, locationZ + tileZ * Tile.TILE_SIZE, 0.2f));
        }
    }

}
