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

/**
 * Mixin for WD pie/cake blocks (wizard_pie, rotten_pie).
 * TODO: Update target class name after checking WD source code.
 */
@Pseudo
@Mixin(targets = "ru.imaginaerum.wd.common.block.PieBlock", remap = false)
public abstract class WDPieBlockMixin {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void overatingplus$guardPieBite(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            net.minecraft.world.phys.BlockHitResult hit,
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
