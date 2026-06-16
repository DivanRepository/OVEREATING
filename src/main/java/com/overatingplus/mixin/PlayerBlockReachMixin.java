package com.overatingplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.world.entity.player.Player;

/**
 * Increases player block reach distance based on oversaturation stacks.
 * Works on both client and server side.
 */
@Mixin(Player.class)
public abstract class PlayerBlockReachMixin {
    
    @Inject(method = "getPickRadius", at = @At("RETURN"), cancellable = true)
    private void overatingplus$increaseBlockReach(CallbackInfoReturnable<Double> cir) {
        Player player = (Player) (Object) this;
        
        double baseRange = cir.getReturnValue();
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        double bonus = OversaturationLogic.getBlockReachBonus(stack);
        
        if (bonus > 0d) {
            cir.setReturnValue(baseRange + bonus);
        }
    }
}
