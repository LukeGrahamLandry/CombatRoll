package net.combatroll.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.combatroll.config.ClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.combatroll.client.MinecraftClientExtension;
import net.combatroll.client.RollManager;
import net.combatroll.client.CombatRollClient;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HudRenderHelper {
    private static final Identifier ARROW = new Identifier("combatroll", "textures/hud/arrow.png");
    private static final Identifier ARROW_BACKGROUND = new Identifier("combatroll", "textures/hud/arrow_background.png");

    public static void render(MatrixStack matrixStack, float tickDelta) {
        ClientConfig config = CombatRollClient.config;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ViewModel viewModel;
        if (player == null) {
            viewModel = ViewModel.mock();
        } else {
            if (player.isCreative() && !config.showHUDInCreative) {
                return;
            }
            if (player.isSpectator()) {
                return;
            }
            RollManager.CooldownInfo cooldownInfo = ((MinecraftClientExtension)client).getRollManager().getCooldown();
            viewModel = ViewModel.create(cooldownInfo, tickDelta);
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        HudElement rollWidget = CombatRollClient.hudConfig.value.rollWidget;
        Vec2f originPoint = rollWidget.origin.getPoint(screenWidth, screenHeight);
        Vec2f drawOffset = rollWidget.offset;

        int horizontalSpacing = 8;
        int biggestTextureSize = 15;
        int widgetWidth = biggestTextureSize + (horizontalSpacing * viewModel.elements.size());
        int widgetHeight = biggestTextureSize;
        int drawX = (int) (originPoint.x + drawOffset.x); // Growing to right by removing `- (widgetWidth) / 2`
        int drawY = (int) (originPoint.y + drawOffset.y - (widgetHeight) / 2);

        RenderSystem.enableBlend();
        for(ViewModel.Element element: viewModel.elements()) {
            int x = 0;
            int y = 0;
            int u = 0;
            int v = 0;
            int width = 0;
            int height = 0;
            int textureSize = 0;

            x = drawX;
            y = drawY;
            u = 0;
            v = 0;
            width = height = textureSize = 15;
            MinecraftClient.getInstance().getTextureManager().bindTexture(ARROW_BACKGROUND);
            RenderSystem.color4f(1, 1, 1, ((float)config.hudBackgroundOpacity) / 100F);
            DrawableHelper.drawTexture(matrixStack, x, y, u, v, width, height, textureSize, textureSize);

            int color = element.color;
            float red = ((float) ((color >> 16) & 0xFF)) / 255F;
            float green = ((float) ((color >> 8) & 0xFF)) / 255F;
            float blue = ((float) (color & 0xFF)) / 255F;

            int prevTextureSize = textureSize;
            textureSize = 13;
            int shift = (prevTextureSize - textureSize) / 2;
            width = textureSize;
            height = Math.round((element.full) * textureSize);
            x = drawX + shift;
            y = drawY + textureSize - height + shift;
            u = 0;
            v = textureSize - height;
            MinecraftClient.getInstance().getTextureManager().bindTexture(ARROW);
            RenderSystem.color4f(red, green, blue, element.full);
            DrawableHelper.drawTexture(matrixStack, x, y, u, v, width, height, textureSize, textureSize);

            drawX += horizontalSpacing;
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static final class ViewModel {
        private final List<Element> elements;

        private ViewModel(List<Element> elements) {
            this.elements = elements;
        }

        static final class Element {
            private final int color;
            private final float full;

            Element(int color, float full) {
                this.color = color;
                this.full = full;
            }

            public int color() {
                return color;
            }

            public float full() {
                return full;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) return true;
                if (obj == null || obj.getClass() != this.getClass()) return false;
                Element that = (Element) obj;
                return this.color == that.color &&
                        Float.floatToIntBits(this.full) == Float.floatToIntBits(that.full);
            }

            @Override
            public int hashCode() {
                return Objects.hash(color, full);
            }

            @Override
            public String toString() {
                return "Element[" +
                        "color=" + color + ", " +
                        "full=" + full + ']';
            }

                }

            static ViewModel create(RollManager.CooldownInfo info, float tickDelta) {
                ClientConfig config = CombatRollClient.config;
                ArrayList<Element> elements = new ArrayList<Element>();
                for (int i = 0; i < info.maxRolls(); ++i) {
                    int color = config.hudArrowColor;
                    float full = 0;
                    if ((i == info.availableRolls())) {
                        full = ((float) info.elapsed()) / ((float) info.total());
                        full = Math.min(full, 1F);

                        if (config.playCooldownFlash) {
                            int missingTicks = info.total() - info.elapsed();
                            int sparkleTicks = 2;
                            if (missingTicks <= sparkleTicks) {
                                float sparkle = ((sparkleTicks / 2) - ((missingTicks - 1 + (1F - tickDelta)) / (sparkleTicks))); // This is really messy, someone improve pls xD
                                float red = ((float) ((color >> 16) & 0xFF)) / 255F;
                                float green = ((float) ((color >> 8) & 0xFF)) / 255F;
                                float blue = ((float) (color & 0xFF)) / 255F;
    //                            System.out.println("Sparkle: " + sparkle + " | info.elapsed():" + info.elapsed() + " | missingTicks:" + missingTicks + " | delta:" + tickDelta);
                                int redBits = (int) (mixNumberFloat(red, 1, sparkle) * 255F);
                                int greenBits = (int) (mixNumberFloat(green, 1, sparkle) * 255F);
                                int blueBits = (int) (mixNumberFloat(blue, 1, sparkle) * 255F);
    //                            System.out.println("Blend -" + " R:" + redBits + " G:" + greenBits + " B:" + blueBits);
                                color = redBits;
                                color = (color << 8) + greenBits;
                                color = (color << 8) + blueBits;
                            }
                        }
                    }
                    if (i < (info.availableRolls())) {
                        full = 1;
                    }
                    elements.add(new Element(color, full));
                }
                return new ViewModel(elements);
            }

            static ViewModel mock() {
                ClientConfig config = CombatRollClient.config;
                int color = config.hudArrowColor;
                return new ViewModel(
                        Arrays.asList(
                                new Element(color, 1),
                                new Element(color, 0.5F),
                                new Element(color, 0)
                        )
                );
            }

        private static float mixNumberFloat(float a, float b, float bias) {
                return a + (b - a) * bias;
            }

        public List<Element> elements() {
            return elements;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            ViewModel that = (ViewModel) obj;
            return Objects.equals(this.elements, that.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(elements);
        }

        @Override
        public String toString() {
            return "ViewModel[" +
                    "elements=" + elements + ']';
        }

        }
}
