package net.combatroll.forge;

import ca.lukegrahamlandry.forgedfabric.FFPlatformHelper;
import net.combatroll.CombatRoll;
import net.combatroll.api.Enchantments_CombatRoll;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.combatroll.client.FabricClientMod;
import net.combatroll.utils.SoundHelper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(CombatRoll.MOD_ID)
public class ForgeMod {
    public ForgeMod() {
        CombatRoll.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new FabricClientMod().onInitializeClient());
        this.register();
    }

    public void register() {
        FFPlatformHelper.register(Registry.ATTRIBUTE, EntityAttributes_CombatRoll.distanceId, EntityAttributes_CombatRoll.DISTANCE);
        FFPlatformHelper.register(Registry.ATTRIBUTE, EntityAttributes_CombatRoll.rechargeId, EntityAttributes_CombatRoll.RECHARGE);
        FFPlatformHelper.register(Registry.ATTRIBUTE, EntityAttributes_CombatRoll.countId, EntityAttributes_CombatRoll.COUNT);

        CombatRoll.configureEnchantments();
        FFPlatformHelper.register(Registry.ENCHANTMENT, Enchantments_CombatRoll.distanceId, Enchantments_CombatRoll.DISTANCE);
        FFPlatformHelper.register(Registry.ENCHANTMENT, Enchantments_CombatRoll.rechargeChestId, Enchantments_CombatRoll.RECHARGE_CHEST);
        FFPlatformHelper.register(Registry.ENCHANTMENT, Enchantments_CombatRoll.rechargeLegsId, Enchantments_CombatRoll.RECHARGE_LEGS);
        FFPlatformHelper.register(Registry.ENCHANTMENT, Enchantments_CombatRoll.countId, Enchantments_CombatRoll.COUNT);

        for (String soundKey: SoundHelper.soundKeys) {
            Identifier id = new Identifier(CombatRoll.MOD_ID, soundKey);
            FFPlatformHelper.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
        }
    }
}