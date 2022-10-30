package net.combatroll.utils;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.combatroll.CombatRoll;

import java.util.Arrays;
import java.util.List;

public class SoundHelper {
    public static List<String> soundKeys = Arrays.asList(
            "roll",
            "roll_cooldown_ready"
    );

    public static void registerSounds() {
        for (String soundKey: soundKeys) {
            Identifier soundId = new Identifier(CombatRoll.MOD_ID, soundKey);
            SoundEvent soundEvent = new SoundEvent(soundId);
            Registry.register(Registry.SOUND_EVENT, soundId, soundEvent);
        }
    }
}
