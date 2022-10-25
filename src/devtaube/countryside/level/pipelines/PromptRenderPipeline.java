package devtaube.countryside.level.pipelines;

import devtaube.countryside.level.BrightFont;
import rosequartz.gfx.GraphicsPipeline;

import java.util.HashMap;

public class PromptRenderPipeline extends GraphicsPipeline {

    private static class Prompt {
        String message;
        boolean shownBefore = false;
        boolean visible = false;
        public Prompt(String message) { this.message = message; }
    }

    private static final HashMap<String, Prompt> prompts = new HashMap<>();

    public static void registerPrompt(String id, String message) { prompts.put(id, new Prompt(message)); }

    // shows the given prompt if it wasn't shown before
    public static void showPrompt(String id) {
        Prompt prompt = prompts.get(id);
        if(prompt.shownBefore) return;
        prompt.visible = true;
        prompt.shownBefore = true;
    }

    // stop displaying a prompt if visible and stops it from reappearing
    public static void stopPrompt(String id) {
        Prompt prompt = prompts.get(id);
        prompt.visible = false;
    }

    // hides the prompt until shown again (if visible)
    public static void hidePrompt(String id) {
        Prompt prompt = prompts.get(id);
        if(!prompt.visible) return;
        prompt.shownBefore = false;
        prompt.visible = false;
    }

    public static final BrightFont PROMPT_RENDER_FONT = new BrightFont();

    public PromptRenderPipeline() {
        add(() -> {
            StringBuilder renderedText = new StringBuilder();
            for(Prompt prompt: prompts.values()) if(prompt.visible) renderedText.append(prompt.message).append("\n");
            float textLineHeight = 0.033f;
            float textTotalWidth = PROMPT_RENDER_FONT.getTextWidth(renderedText.toString(), textLineHeight, textLineHeight / 7);
            float textX = (1 - textTotalWidth) / 2;
            float textY = 0.025f;
            PROMPT_RENDER_FONT.render(renderedText.toString(), textX, textY, textLineHeight, textLineHeight / 7, textLineHeight / 7);
        });
    }

}
