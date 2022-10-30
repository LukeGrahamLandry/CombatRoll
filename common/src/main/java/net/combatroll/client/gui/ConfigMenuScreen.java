package net.combatroll.client.gui;

import me.shedaniel.autoconfig.AutoConfig;
import net.combatroll.config.ClientConfigWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ConfigMenuScreen extends Screen {
    private Screen previous;

    public ConfigMenuScreen(Screen parent) {
        super(new TranslatableText("gui.combatroll.config_menu"));
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
        addButton(new ButtonWidget(buttonCenterX, buttonCenterY, buttonWidth, buttonHeight, new TranslatableText("gui.combatroll.settings"), button -> {
            client.openScreen(AutoConfig.getConfigScreen(ClientConfigWrapper.class, this).get());
        }));
        addButton(new ButtonWidget(buttonCenterX, buttonCenterY + 30, buttonWidth, buttonHeight, new TranslatableText("gui.combatroll.hud"), button -> {
            client.openScreen(new HudConfigScreen(this));
        }));
    }

    public void close() {
        this.client.openScreen(previous);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
