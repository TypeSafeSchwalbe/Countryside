package devtaube.countryside.level.entities;

import devtaube.countryside.level.components.NoteRenderComponent;
import rosequartz.ecb.Entity;

public class RenderedNote extends Entity {

    public RenderedNote(int id) {
        add(new NoteRenderComponent(id));
    }

}
