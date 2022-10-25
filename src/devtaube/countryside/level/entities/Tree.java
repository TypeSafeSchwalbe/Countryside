package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.ModelComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.SpherePlayerColliderComponent;
import devtaube.countryside.level.components.TilePositionComponent;
import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Entity;
import rosequartz.gfx.VertexArray;

public class Tree extends Entity {

    public Tree(VertexArray treeArray, float x, float z, int parentTileX, int parentTileZ, SphereCollider[] colliders) {
        add(
                new PositionComponent(x, 0, z),
                new TilePositionComponent(parentTileX, parentTileZ),
                new ModelComponent(treeArray, "models/tree.png", "models/tree_bright.png"),
                new SpherePlayerColliderComponent(colliders)
        );
    }

}
