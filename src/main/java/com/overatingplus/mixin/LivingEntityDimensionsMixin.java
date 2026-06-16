package com.overatingplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.overatingplus.logic.OversaturationSizeLogic;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDimensionsMixin {
    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void overatingplus$oversaturationDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) {
            return;
        }
        if (!OversaturationSizeLogic.shouldApplySize(player)) {
            return;
        }
        cir.setReturnValue(OversaturationSizeLogic.scaleDimensions(player, pose, cir.getReturnValue()));
    }
}
