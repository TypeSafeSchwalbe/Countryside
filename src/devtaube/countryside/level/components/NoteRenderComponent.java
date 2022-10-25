package devtaube.countryside.level.components;

import rosequartz.ecb.Component;

public class NoteRenderComponent implements Component {

    public final int note;
    public int page;

    public NoteRenderComponent(int note) {
        this.note = note;
        page = 0;
    }

    @Override
    public Component copy() {
        NoteRenderComponent clone = new NoteRenderComponent(note);
        clone.page = page;
        return clone;
    }

}
