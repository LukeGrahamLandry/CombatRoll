package net.combatroll.mixin;

import net.combatroll.CombatRoll;
import net.combatroll.config.ServerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.combatroll.client.MinecraftClientExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Shadow
    @Final
    protected MinecraftClient client;

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(Z)V", shift = At.Shift.AFTER))
    private void tickMovement_ModifyInput(CallbackInfo ci) {
        MinecraftClientExtension client = (MinecraftClientExtension) MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayer = (ClientPlayerEntity) ((Object) this);
        ServerConfig config = CombatRoll.config;
        if (!config.allow_jump_while_rolling && client.getRollManager().isRolling()) {
            clientPlayer.input.jumping = false;
        }
    }

    @Inject(method = "shouldAutoJump", at = @At("HEAD"), cancellable = true)
    public void shouldAutoJump_HEAD(CallbackInfoReturnable<Boolean> cir) {
        ServerConfig config = CombatRoll.config;
        if (config != null) {
            MinecraftClientExtension client = (MinecraftClientExtension) MinecraftClient.getInstance();
            if (client.getRollManager().isRolling() && !config.allow_auto_jump_while_rolling) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}