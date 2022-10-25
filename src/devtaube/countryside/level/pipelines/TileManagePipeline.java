package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.TileGenerator;
import devtaube.countryside.level.components.PlayerComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.TilePositionComponent;
import devtaube.countryside.level.entities.Tile;
import rosequartz.ecb.ECB;
import rosequartz.gfx.GraphicsPipeline;

import java.util.ArrayList;

public class TileManagePipeline extends GraphicsPipeline {

    public TileManagePipeline() {
        add(() -> {
            int[] playerTilePosition = new int[2];
            ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> player.<PositionComponent>get(PositionComponent.class, positionComponent -> {
                playerTilePosition[0] = Math.round(positionComponent.x / Tile.TILE_SIZE);
                playerTilePosition[1] = Math.round(positionComponent.z / Tile.TILE_SIZE);
            }));
            ArrayList<int[]> neededTilePositions = new ArrayList<>();
            for(int tileX = playerTilePosition[0] - 2; tileX < playerTilePosition[0] + 2; tileX++)
                for(int tileZ = playerTilePosition[1] - 2; tileZ < playerTilePosition[1] + 2; tileZ++)
                    neededTilePositions.add(new int[] { tileX, tileZ });
            ArrayList<int[]> unfoundTilePositions = new ArrayList<>(neededTilePositions);
            ECB.<TilePositionComponent>get(TilePositionComponent.class, (tileObject, tilePositionComponent) -> {
                for(int[] neededTilePosition: neededTilePositions) {
                    if(neededTilePosition[0] != tilePositionComponent.x || neededTilePosition[1] != tilePositionComponent.z) continue;
                    unfoundTilePositions.remove(neededTilePosition);
                    return;
                }
                ECB.remove(tileObject);
            });
            for(int[] neededTilePosition: unfoundTilePositions) TileGenerator.generateTile(neededTilePosition[0], neededTilePosition[1]);
        });
    }

}
