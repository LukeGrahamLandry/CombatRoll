package net.combatroll.config;

import net.minecraft.util.math.Vec2f;
import net.combatroll.client.gui.HudElement;

public class HudConfig {
    public HudElement rollWidget;

    public static HudConfig createDefault() {
        HudConfig config = new HudConfig();
        config.rollWidget = createDefaultRollWidget();
        return config;
    }

    public static HudElement createDefaultRollWidget() {
        HudElement.Origin origin = HudElement.Origin.BOTTOM;
        Vec2f offset = new Vec2f(origin.initialOffset().x + 100, origin.initialOffset().y);
        return new HudElement(origin, offset);
    }
}
