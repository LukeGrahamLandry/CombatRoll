package net.combatroll.mixin;

import net.combatroll.CombatRoll;
import net.combatroll.Platform;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.combatroll.client.MinecraftClientExtension;
import net.combatroll.client.RollEffect;
import net.combatroll.client.RollKeybings;
import net.combatroll.client.RollManager;
import net.combatroll.compatibility.BetterCombatHelper;
import net.combatroll.network.Packets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.combatroll.api.EntityAttributes_CombatRoll.Type.DISTANCE;
import static net.combatroll.client.RollEffect.Particles.PUFF;

@Mixin(value = MinecraftClient.class, priority = 449)
public abstract class MinecraftClientMixin implements MinecraftClientExtension {
    @Shadow private int itemUseCooldown;
    @Shadow @Nullable public ClientPlayerEntity player;
    private RollManager rollManager = new RollManager();
    public RollManager getRollManager() {
        return rollManager;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",at = @At("TAIL"))
    private void disconnect_TAIL(Screen screen, CallbackInfo ci) {
        rollManager.isEnabled = false;
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void doAttack_HEAD(CallbackInfo info) {
        if (rollManager.isRolling()) {
            info.cancel();
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void handleBlockBreaking_HEAD(boolean bl, CallbackInfo ci) {
        if (rollManager.isRolling()) {
            ci.cancel();
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void doItemUse_HEAD(CallbackInfo ci) {
        if (rollManager.isRolling()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick_TAIL(CallbackInfo ci) {
        tryRolling();
    }

    private void tryRolling() {
        MinecraftClient client = (MinecraftClient) ((Object)this);
        if (player == null || client.isPaused()) {
            return;
        }
        rollManager.tick(player);
        if (RollKeybings.roll.isPressed()) {
            if (Platform.Forge && client.currentScreen != null) {
                return;
            }
            if(!rollManager.isRollAvailable()) {
                return;
            }
            if(!CombatRoll.config.allow_rolling_while_airborn && !player.isOnGround()) {
                return;
            }
            if(player.isSwimming()) {
                return;
            }
            if(player.getVehicle() != null) {
                return;
            }
            if(player.isUsingItem() || player.isBlocking()) {
                return;
            }
            if (!CombatRoll.config.allow_rolling_while_weapon_cooldown && player.getAttackCooldownProgress(0) < 0.95) {
                return;
            }
            if (BetterCombatHelper.isDoingUpswing()) {
                BetterCombatHelper.cancelUpswing();
            } else {
                if (client.options.keyAttack.isPressed()) {
                    return;
                }
            }
            if (itemUseCooldown > 0) {
                return;
            }

            float forward = player.input.movementForward;
            float sideways = player.input.movementSideways;
            Vec3d direction;
            if (forward == 0 && sideways == 0) {
                direction = new Vec3d(0, 0, 1);
            } else  {
                direction = new Vec3d(sideways, 0, forward).normalize();
            }
            direction = direction.rotateY((float) Math.toRadians((-1.0) * player.getYaw(1)));
            double distance = 0.475 *
                    (EntityAttributes_CombatRoll.getAttributeValue(player, DISTANCE)
                    + CombatRoll.config.additional_roll_distance);
            direction = direction.multiply(distance);

            Block block = player.world.getBlockState(player.getBlockPos().down()).getBlock();
            float slipperiness = block.getSlipperiness();
            float defaultSlipperiness = Blocks.GRASS.getSlipperiness();
            if (slipperiness > defaultSlipperiness) {
                float multiplier = defaultSlipperiness / slipperiness;
                direction = direction.multiply(multiplier * multiplier);
            }

            player.addVelocity(direction.x, direction.y, direction.z);
            rollManager.onRoll(player);

            RollEffect.Visuals rollVisuals = new RollEffect.Visuals(CombatRoll.MOD_ID + ":roll", PUFF);
            ClientPlayNetworking.send(
                    Packets.RollPublish.ID,
                    new Packets.RollPublish(player.getEntityId(), rollVisuals, direction).write());
            RollEffect.playVisuals(rollVisuals, player, direction);
        }
    }
}
