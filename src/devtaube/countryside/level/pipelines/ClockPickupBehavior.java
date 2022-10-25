package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.Level;
import devtaube.countryside.level.components.*;
import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;

public class ClockPickupBehavior implements Behavior {

    static { PromptRenderPipeline.registerPrompt("clock-pickup", "[E]/[LMB]/(A) pick up clock"); }

    private boolean pickedUp = false;

    @Override
    public void run() {
        SphereCollider playerCollider = new SphereCollider(0, 0, 0, 0.2f);
        ECB.get(PlayerComponent.class, (player, ignored) -> player.get(PositionComponent.class, playerPositionComponent -> playerCollider.setPosition(playerPositionComponent.x, playerPositionComponent.y, playerPositionComponent.z)));
        boolean[] collisionFound = { false };
        ECB.get(ClockPickupColliderComponent.class, (clockPickup, clockPickupColliderComponent) -> {
            clockPickup.get(PositionComponent.class, positionComponent -> clockPickupColliderComponent.collider.setPosition(positionComponent.x, positionComponent.y, positionComponent.z));
            if(clockPickupColliderComponent.collider.getColliding(playerCollider)) {
                PromptRenderPipeline.showPrompt("clock-pickup");
                collisionFound[0] = true;
                if(Level.inputPrimary) {
                    PromptRenderPipeline.stopPrompt("clock-pickup");
                    ECB.remove(clockPickup);
                    ECB.get(PlayerComponent.class, (player, playerComponent) -> player.add(new HasClockComponent()));
                    pickedUp = true;
                }
            }
        });
        if(!collisionFound[0]) PromptRenderPipeline.hidePrompt("clock-pickup");
        if(pickedUp) ECB.get(ClockPickupColliderComponent.class, (entity, clockPickupColliderComponent) -> ECB.remove(entity));
    }

}
