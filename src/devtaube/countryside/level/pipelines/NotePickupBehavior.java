package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.Level;
import devtaube.countryside.level.components.*;
import devtaube.countryside.level.entities.RenderedNote;
import rosequartz.coll.SphereCollider;
import rosequartz.ecb.Behavior;
import rosequartz.ecb.ECB;

public class NotePickupBehavior implements Behavior {

    static { PromptRenderPipeline.registerPrompt("note-pickup", "[E]/[LMB]/(A) read note"); }

    static int nextPickupNote = 0;

    public static boolean pickedUpThisFrame = false;

    public NotePickupBehavior() { nextPickupNote = 0; }

    @Override
    public void run() {
        ECB.<NotePickupColliderComponent>get(NotePickupColliderComponent.class, (notePickup, notePickupColliderComponent) -> {
            notePickup.get(PositionComponent.class, (PositionComponent positionComponent) -> notePickupColliderComponent.collider.setPosition(positionComponent.x, positionComponent.y, positionComponent.z));
        });
        pickedUpThisFrame = false;
        SphereCollider playerCollider = new SphereCollider(0, 0, 0, 0.2f);
        ECB.<PlayerComponent>get(PlayerComponent.class, (player, playerComponent) -> player.get(PositionComponent.class, (PositionComponent playerPositionComponent) -> playerCollider.setPosition(playerPositionComponent.x, playerPositionComponent.y, playerPositionComponent.z)));
        boolean[] collisionFound = { false };
        ECB.<NotePickupColliderComponent>get(NotePickupColliderComponent.class, (notePickup, notePickupColliderComponent) -> {
            if(notePickupColliderComponent.collider.getColliding(playerCollider)) {
                PromptRenderPipeline.showPrompt("note-pickup");
                collisionFound[0] = true;
                if(Level.inputPrimary) {
                    boolean[] renderedNote = { false };
                    ECB.<NoteRenderComponent>get(NoteRenderComponent.class, (renderedNoteEntity, noteRenderComponent) -> renderedNote[0] = true);
                    if(!renderedNote[0]) {
                        PromptRenderPipeline.stopPrompt("note-pickup");
                        pickedUpThisFrame = true;
                        if(notePickup.has(NoteTypeComponent.class)) ECB.add(new RenderedNote(notePickup.<NoteTypeComponent>get(NoteTypeComponent.class).id));
                        else {
                            notePickup.add(new NoteTypeComponent(nextPickupNote));
                            ECB.add(new RenderedNote(nextPickupNote));
                            nextPickupNote++;
                        }
                    }
                }
            }
        });
        if(!collisionFound[0]) PromptRenderPipeline.hidePrompt("note-pickup");
        if(nextPickupNote >= NoteRenderPipeline.NOTE_COUNT) ECB.<NotePickupColliderComponent>get(NotePickupColliderComponent.class, (entity, notePickupColliderComponent) -> { if(!entity.has(NoteTypeComponent.class)) ECB.remove(entity); });
    }

}
