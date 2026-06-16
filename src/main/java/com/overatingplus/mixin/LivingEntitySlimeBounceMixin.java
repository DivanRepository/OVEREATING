package com.overatingplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Mixin(LivingEntity.class)
public abstract class LivingEntitySlimeBounceMixin {
    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void overatingplus$noFallDamage(
            float fallDistance,
            float damageMultiplier,
            DamageSource damageSource,
            CallbackInfoReturnable<Boolean> cir
    ) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) {
            return;
        }
        if (self instanceof Player player && OversaturationLogic.canSlimeBounce(player)) {
            self.resetFallDistance();
            cir.setReturnValue(false);
        }
    }
}
