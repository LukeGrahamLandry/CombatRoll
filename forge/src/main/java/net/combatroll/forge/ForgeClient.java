package net.combatroll.forge;

import net.combatroll.CombatRoll;
import net.combatroll.client.gui.ConfigMenuScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CombatRoll.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, modScreen) -> new ConfigMenuScreen(modScreen));
    }
}
