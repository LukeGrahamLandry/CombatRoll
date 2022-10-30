package net.combatroll.client.gui;

import net.minecraft.util.math.Vec2f;

public class HudElement {
    public Origin origin;
    public Vec2f offset;

    public HudElement(Origin origin, Vec2f offset) {
        this.origin = origin;
        this.offset = offset;
    }

    public enum Origin {
        TOP, TOP_LEFT, TOP_RIGHT,
        BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT;

        public Vec2f getPoint(int screenWidth, int screenHeight) {
            if (this == Origin.TOP) {
                return new Vec2f(screenWidth / 2F, 0);
            } else if (this == Origin.TOP_LEFT) {
                return new Vec2f(0, 0);
            } else if (this == Origin.TOP_RIGHT) {
                return new Vec2f(screenWidth, 0);
            } else if (this == Origin.BOTTOM) {
                return new Vec2f(screenWidth / 2F, screenHeight);
            } else if (this == Origin.BOTTOM_LEFT) {
                return new Vec2f(0, screenHeight);
            } else if (this == Origin.BOTTOM_RIGHT) {
                return new Vec2f(screenWidth, screenHeight);
            }
            return new Vec2f(screenWidth / 2F, screenHeight / 2F); // Should never run
        }

        public Vec2f initialOffset() {
            int offset = 12;
            if (this == Origin.TOP) {
                return new Vec2f(0, offset);
            } else if (this == Origin.TOP_LEFT) {
                return new Vec2f(offset, offset);
            } else if (this == Origin.TOP_RIGHT) {
                return new Vec2f((-1) * offset, offset);
            } else if (this == Origin.BOTTOM) {
                return new Vec2f(0, (-1) * offset);
            } else if (this == Origin.BOTTOM_LEFT) {
                return new Vec2f(offset, (-1) * offset);
            } else if (this == Origin.BOTTOM_RIGHT) {
                return new Vec2f((-1) * offset, (-1) * offset);
            }
            return new Vec2f(0, 0); // Should never run
        }
    }
}
