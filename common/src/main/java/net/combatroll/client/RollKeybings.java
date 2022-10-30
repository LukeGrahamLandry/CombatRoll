package net.combatroll.client;

import net.combatroll.CombatRoll;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class RollKeybings {
    public static KeyBinding roll;
    public static List<KeyBinding> all;

    static {
        roll = new KeyBinding(
                "keybinds.combatroll.roll",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CombatRoll.modName());

        all = Arrays.asList(roll);
    }
}
