package com.overatingplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public abstract class PlayerCanEatMixin {
    @Inject(method = "canEat", at = @At("RETURN"), cancellable = true)
    private void overatingplus$allowEatingWhenHungerFull(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || canAlwaysEat) {
            return;
        }
        Player player = (Player) (Object) this;
        if (player.getFoodData().getFoodLevel() >= 20) {
            cir.setReturnValue(true);
        }
    }
}
