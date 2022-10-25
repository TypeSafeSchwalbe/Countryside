package devtaube.countryside.level.components;

import devtaube.countryside.level.entities.Tile;
import rosequartz.ecb.Component;

public class EnemyVisibilityBlockComponent implements Component {

    public static final float BLOCKED_DISTANCE = 10;
    public static final float MINIMUM_SEEN_DISTANCE = Tile.TILE_SIZE / 3;

    public boolean seenByPlayer = false;

    @Override
    public EnemyVisibilityBlockComponent copy() {
        EnemyVisibilityBlockComponent clone = new EnemyVisibilityBlockComponent();
        clone.seenByPlayer = seenByPlayer;
        return clone;
    }

}
