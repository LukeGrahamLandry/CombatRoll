package net.combatroll.api;

import net.combatroll.CombatRoll;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public class EntityAttributes_CombatRoll {

    public static final String distanceName = "distance";
    public static final Identifier distanceId = new Identifier(CombatRoll.MOD_ID + ":" + distanceName);
    public static final EntityAttribute DISTANCE = (new ClampedEntityAttribute("attribute.name.combat_roll." + distanceName, 3.0, 1, 24.0)).setTracked(true);

    public static final String rechargeName = "recharge";
    public static final Identifier rechargeId = new Identifier(CombatRoll.MOD_ID + ":" + rechargeName);
    public static final EntityAttribute RECHARGE = (new ClampedEntityAttribute("attribute.name.combat_roll." + rechargeName, 20, 0.1, 200)).setTracked(true);

    public static final String countName = "count";
    public static final Identifier countId = new Identifier(CombatRoll.MOD_ID + ":" + countName);
    public static final EntityAttribute COUNT = (new ClampedEntityAttribute("attribute.name.combat_roll." + countName, 1, 1, 20.0)).setTracked(true);

    public static List<EntityAttribute> all;
    static {
        all = Arrays.asList(DISTANCE, RECHARGE, COUNT);
    }

    // Helper

    public enum Type {
        DISTANCE, RECHARGE, COUNT
    }

    public static double getAttributeValue(PlayerEntity player, Type type) {
        if (type == Type.DISTANCE) {
            double value = player.getAttributeValue(DISTANCE);
            int level = EnchantmentHelper.getEquipmentLevel(Enchantments_CombatRoll.DISTANCE, player);
            value = Enchantments_CombatRoll.DISTANCE.apply(value, level);
            return value;
        } else if (type == Type.RECHARGE) {
            double value = player.getAttributeValue(RECHARGE);
            int chestLevel = EnchantmentHelper.getEquipmentLevel(Enchantments_CombatRoll.RECHARGE_CHEST, player);
            value = Enchantments_CombatRoll.RECHARGE_CHEST.apply(value, chestLevel);
            int legsLevel = EnchantmentHelper.getEquipmentLevel(Enchantments_CombatRoll.RECHARGE_LEGS, player);
            value = Enchantments_CombatRoll.RECHARGE_LEGS.apply(value, legsLevel);
            return value;
        } else if (type == Type.COUNT) {
            double value = player.getAttributeValue(COUNT);
            int level = EnchantmentHelper.getEquipmentLevel(Enchantments_CombatRoll.COUNT, player);
            value = Enchantments_CombatRoll.COUNT.apply(value, level);
            return value;
        }
        return 1; // Should never happen
    }
}
