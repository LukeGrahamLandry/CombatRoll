package net.combatroll.client;

import net.combatroll.CombatRoll;
import net.combatroll.config.ServerConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.combatroll.network.Packets;

public class ClientNetwork {
    public static void initializeHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(Packets.RollAnimation.ID, (client, handler, buf, responseSender) -> {
            final Packets.RollAnimation packet = Packets.RollAnimation.read(buf);
            client.execute(() -> {
                Entity entity = client.world.getEntityById(packet.playerId());
                if (entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    RollEffect.playVisuals(packet.visuals(), player, packet.velocity());
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Packets.ConfigSync.ID, (client, handler, buf, responseSender) -> {
            ServerConfig config = Packets.ConfigSync.read(buf);
            ((MinecraftClientExtension)client).getRollManager().isEnabled = true;
            CombatRoll.config = config;
        });
    }
}
