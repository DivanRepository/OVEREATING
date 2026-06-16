package com.overatingplus.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.overatingplus.logic.OversaturationFoodLogic;
import com.overatingplus.logic.PlaceableFoodContext;
import com.overatingplus.logic.PlaceableFoodLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(targets = "vectorwing.farmersdelight.common.block.PieBlock", remap = false)
public abstract class FarmersDelightPieBlockMixin {
    @Inject(method = "consumeBite", at = @At("HEAD"), cancellable = true, remap = false)
    private void overatingplus$guardPieBite(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        var block = state.getBlock();
        if (!PlaceableFoodLogic.isPlaceableFood(block)) {
            return;
        }

        if (OversaturationFoodLogic.shouldBlockRecentFood(player, PlaceableFoodLogic.getFoodId(block))) {
            if (!level.isClientSide()) {
                OversaturationFoodLogic.notifyWantSomethingElse(player);
            }
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        if (!level.isClientSide()) {
            PlaceableFoodContext.begin(player, block);
        }
    }
}
