package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.Level;
import devtaube.countryside.level.components.HasRadioComponent;
import devtaube.countryside.level.components.PlayerComponent;
import devtaube.countryside.level.components.PositionComponent;
import devtaube.countryside.level.components.RadioPickupColliderComponent;
import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;

public class RadioPickupBehavior implements Behavior {

    static { PromptRenderPipeline.registerPrompt("radio-pickup", "[E]/[LMB]/(A) pick up radio"); }

    private boolean pickedUp = false;

    @Override
    public void run() {
        SphereCollider playerCollider = new SphereCollider(0, 0, 0, 0.2f);
        ECB.<PlayerComponent>get(PlayerComponent.class, (player, ignored) -> player.get(PositionComponent.class, (PositionComponent playerPositionComponent) -> playerCollider.setPosition(playerPositionComponent.x, playerPositionComponent.y, playerPositionComponent.z)));
        boolean[] collisionFound = { false };
        ECB.<RadioPickupColliderComponent>get(RadioPickupColliderComponent.class, (radioPickup, radioPickupColliderComponent) -> {
            radioPickup.get(PositionComponent.class, (PositionComponent positionComponent) -> radioPickupColliderComponent.collider.setPosition(positionComponent.x, positionComponent.y, positionComponent.z));
            if(radioPickupColliderComponent.collider.getColliding(playerCollider)) {
                PromptRenderPipeline.showPrompt("radio-pickup");
                collisionFound[0] = true;
                if(Level.inputPrimary) {
                    PromptRenderPipeline.stopPrompt("radio-pickup");
                    ECB.remove(radioPickup);
                    ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> player.add(new HasRadioComponent()));
                    pickedUp = true;
                }
            }
        });
        if(!collisionFound[0]) PromptRenderPipeline.hidePrompt("radio-pickup");
        if(pickedUp) ECB.<RadioPickupColliderComponent>get(RadioPickupColliderComponent.class, (entity, radioPickupColliderComponent) -> ECB.remove(entity));
    }

}
