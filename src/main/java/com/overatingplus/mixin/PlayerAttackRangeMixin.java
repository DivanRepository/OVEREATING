package com.overatingplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.world.entity.player.Player;

/**
 * Increases player attack range based on oversaturation stacks.
 * Works on both client and server side.
 */
@Mixin(Player.class)
public abstract class PlayerAttackRangeMixin {
    
    @Inject(method = "getCurrentAttackReach", at = @At("RETURN"), cancellable = true)
    private void overatingplus$increaseAttackRange(CallbackInfoReturnable<Float> cir) {
        Player player = (Player) (Object) this;
        
        float baseRange = cir.getReturnValue();
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        double bonus = OversaturationLogic.getAttackDistanceBonus(stack);
        
        if (bonus > 0d) {
            cir.setReturnValue((float) (baseRange + bonus));
        }
    }
}
