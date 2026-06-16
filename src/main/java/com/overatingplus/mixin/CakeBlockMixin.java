package com.overatingplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
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
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void overatingplus$guardPlaceableFood(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!PlaceableFoodLogic.isPlaceableFood(state)) {
            return;
        }

        if (OversaturationFoodLogic.shouldBlockRecentFood(player, PlaceableFoodLogic.getFoodId(state.getBlock()))) {
            if (!level.isClientSide()) {
                OversaturationFoodLogic.notifyWantSomethingElse(player);
            }
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        if (!level.isClientSide()) {
            PlaceableFoodContext.begin(player, state.getBlock());
        }
    }
}
