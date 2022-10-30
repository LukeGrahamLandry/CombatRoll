package net.combatroll.client.gui;

import net.combatroll.client.CombatRollClient;
import net.combatroll.config.HudConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec2f;

public class HudConfigScreen extends Screen {
    private Screen previous;

    public HudConfigScreen(Screen previous) {
        super(new TranslatableText("gui.combatroll.hud"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int buttonCenterX = (width / 2) - (buttonWidth / 2);
        int buttonCenterY = (height / 2) - (buttonHeight / 2);

        addButton(new ButtonWidget(buttonCenterX, buttonCenterY - 30, buttonWidth, buttonHeight, new TranslatableText("gui.combatroll.close"), button -> {
            close();
        }));
        addButton(new ButtonWidget(buttonCenterX, buttonCenterY, buttonWidth, buttonHeight, new TranslatableText("gui.combatroll.corner"), button -> {
            nextOrigin();
        }));
        addButton(new ButtonWidget(buttonCenterX, buttonCenterY + 30, buttonWidth, buttonHeight, new TranslatableText("gui.combatroll.reset"), button -> {
            reset();
        }));
    }

    public void close() {
        this.save();
        this.client.openScreen(previous);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        HudRenderHelper.render(matrices, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!this.isDragging() && button == 0) {
            HudConfig config = CombatRollClient.hudConfig.value;
            config.rollWidget.offset = new Vec2f(
                    (float) (config.rollWidget.offset.x + deltaX),
                    (float) (config.rollWidget.offset.y + deltaY));
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public static void nextOrigin() {
        HudConfig config = CombatRollClient.hudConfig.value;
        HudElement.Origin origin;
        try {
            origin = HudElement.Origin.values()[(config.rollWidget.origin.ordinal() + 1)];
            config.rollWidget = new HudElement(origin, origin.initialOffset());
        } catch (Exception e) {
            origin = HudElement.Origin.values()[0];
            config.rollWidget = new HudElement(origin, origin.initialOffset());
        }
    }

    public void save() {
        CombatRollClient.hudConfig.save();
    }

    public void reset() {
        HudConfig config = CombatRollClient.hudConfig.value;
        config.rollWidget = HudConfig.createDefaultRollWidget();
    }
}
