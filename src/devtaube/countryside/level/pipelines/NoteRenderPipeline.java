package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.Level;
import devtaube.countryside.level.NoteRenderFont;
import devtaube.countryside.level.components.NoteRenderComponent;
import rosequartz.ecb.ECB;
import rosequartz.files.Resource;
import rosequartz.gfx.Graphics;
import rosequartz.gfx.GraphicsPipeline;
import rosequartz.gfx.Texture;

import java.util.Arrays;

public class NoteRenderPipeline extends GraphicsPipeline {

    static { PromptRenderPipeline.registerPrompt("note-nextpage", "[E]/[LMB]/(A) next page"); }

    public static final int NOTE_COUNT = 7;
    public static final String[][] NOTE_CONTENTS = loadNoteContents();

    public static final Texture NOTE_BACKGROUND = new Texture(new Resource("notes/background.png"));
    public static final Texture NOTE_BACKGROUND_BLOOD = new Texture(new Resource("notes/background_blood.png"));

    public static final NoteRenderFont NOTE_RENDER_FONT = new NoteRenderFont();

    private static String[][] loadNoteContents() {
        String[][] result = new String[NOTE_COUNT][];
        for(int noteIndex = 0; noteIndex < NOTE_COUNT; noteIndex++) {
            String[] noteLines = new Resource("notes/" + noteIndex + ".txt").forget().toString().split("(\\r\\n|\\n|\\r)");
            String[] notePages = new String[noteLines.length / 18 + 1];
            Arrays.fill(notePages, "");
            for(int lineIndex = 0; lineIndex < noteLines.length; lineIndex++) notePages[lineIndex / 18] = notePages[lineIndex / 18] + noteLines[lineIndex] + "\n";
            result[noteIndex] = notePages;
        }
        return result;
    }

    public NoteRenderPipeline() {
        add(() -> ECB.get(NoteRenderComponent.class, (noteRender, noteRenderComponent) -> {
            PromptRenderPipeline.showPrompt("note-nextpage");
            if(Level.inputPrimary && !NotePickupBehavior.pickedUpThisFrame) {
                PromptRenderPipeline.stopPrompt("note-nextpage");
                noteRenderComponent.page++;
                if(noteRenderComponent.page >= NOTE_CONTENTS[noteRenderComponent.note].length)
                    ECB.remove(noteRender);
            }
        }));
        add(() -> ECB.get(NoteRenderComponent.class, (noteRender, noteRenderComponent) -> {
            if(NOTE_CONTENTS[noteRenderComponent.note][noteRenderComponent.page].equals("")) ECB.remove(noteRender);
            float noteHeight = 0.75f;
            float noteWidth = noteHeight * NOTE_BACKGROUND.getWidth() / NOTE_BACKGROUND.getHeight() * Graphics.windowHeight() / Graphics.windowWidth();
            float noteX1 = (1 - noteWidth) / 2;
            float noteY1 = (1 - noteHeight) / 2;
            float noteX2 = noteX1 + noteWidth;
            float noteY2 = noteY1 + noteHeight;
            if(noteRenderComponent.note >= NOTE_COUNT - 1) NOTE_BACKGROUND_BLOOD.blit(0, 0, 1, 1, noteX1, noteY1, noteX2, noteY2);
            else NOTE_BACKGROUND.blit(0, 0, 1, 1, noteX1, noteY1, noteX2, noteY2);
            float height = noteHeight * 7 / NOTE_BACKGROUND.getHeight();
            float spacingX = (noteX2 - noteX1) / NOTE_BACKGROUND.getWidth();
            float spacingY = (noteY2 - noteY1) / NOTE_BACKGROUND.getHeight();
            float textY = noteY1 - spacingY + noteHeight - NOTE_RENDER_FONT.getTextHeight(NOTE_CONTENTS[noteRenderComponent.note][noteRenderComponent.page], height, spacingY);
            NOTE_RENDER_FONT.render(NOTE_CONTENTS[noteRenderComponent.note][noteRenderComponent.page], noteX1 + spacingX, textY, height, spacingX, spacingY);
        }));
    }

}
