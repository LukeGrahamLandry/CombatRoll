package net.combatroll.client;

import net.combatroll.CombatRoll;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

import static net.combatroll.api.EntityAttributes_CombatRoll.Type.COUNT;
import static net.combatroll.api.EntityAttributes_CombatRoll.Type.RECHARGE;

public class RollManager {
    public boolean isEnabled = false;
    public static int rollDuration() {
        return CombatRoll.config.roll_duration;
    }
    private int timeSinceLastRoll = 10;
    private int currentCooldownProgress = 0;
    private int currentCooldownLength = 0;
    private int maxRolls = 1;
    private int availableRolls = 0;

    public RollManager() { }

    public static final class CooldownInfo {
        private final int elapsed;
        private final int total;
        private final int availableRolls;
        private final int maxRolls;

        public CooldownInfo(int elapsed, int total, int availableRolls, int maxRolls) {
            this.elapsed = elapsed;
            this.total = total;
            this.availableRolls = availableRolls;
            this.maxRolls = maxRolls;
        }

        public int elapsed() {
            return elapsed;
        }

        public int total() {
            return total;
        }

        public int availableRolls() {
            return availableRolls;
        }

        public int maxRolls() {
            return maxRolls;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            CooldownInfo that = (CooldownInfo) obj;
            return this.elapsed == that.elapsed &&
                    this.total == that.total &&
                    this.availableRolls == that.availableRolls &&
                    this.maxRolls == that.maxRolls;
        }

        @Override
        public int hashCode() {
            return Objects.hash(elapsed, total, availableRolls, maxRolls);
        }

        @Override
        public String toString() {
            return "CooldownInfo[" +
                    "elapsed=" + elapsed + ", " +
                    "total=" + total + ", " +
                    "availableRolls=" + availableRolls + ", " +
                    "maxRolls=" + maxRolls + ']';
        }
    }

    public CooldownInfo getCooldown() {
        return new CooldownInfo(currentCooldownProgress, currentCooldownLength, availableRolls, maxRolls);
    }

    public boolean isRollAvailable() {
        return isEnabled && !isRolling() && availableRolls > 0;
    }

    public boolean isRolling() {
        return timeSinceLastRoll <= rollDuration();
    }

    public void onRoll(ClientPlayerEntity player) {
        availableRolls -= 1;
        timeSinceLastRoll = 0;
        updateCooldownLength(player);
    }

    public void tick(ClientPlayerEntity player) {
        maxRolls = (int) EntityAttributes_CombatRoll.getAttributeValue(player, COUNT);
        timeSinceLastRoll += 1;
        if (availableRolls < maxRolls) {
            currentCooldownProgress += 1;
            if (currentCooldownProgress >= currentCooldownLength) {
                rechargeRoll(player);
            }
        }
        if (availableRolls == maxRolls) {
            currentCooldownProgress = 0;
        }
        if (availableRolls > maxRolls) {
            availableRolls = maxRolls;
        }
    }

    private void rechargeRoll(ClientPlayerEntity player) {
        availableRolls += 1;
        currentCooldownProgress = 0;
        updateCooldownLength(player);
        if (CombatRollClient.config.playCooldownSound) {
            SoundEvent cooldownReady = Registry.SOUND_EVENT.get(new Identifier("combatroll:roll_cooldown_ready"));
            if (cooldownReady != null) {
                player.world.playSound(player.getX(), player.getY(), player.getZ(), cooldownReady, SoundCategory.PLAYERS, 1, 1, false);
            }
        }
    }

    private void updateCooldownLength(ClientPlayerEntity player) {
        float duration = CombatRoll.config.roll_cooldown;
        currentCooldownLength = (int) Math.round(duration * 20F * (20F / EntityAttributes_CombatRoll.getAttributeValue(player, RECHARGE)));
    }
}
