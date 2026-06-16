package com.overatingplus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.logic.OversaturationFoodLogic;
import com.overatingplus.logic.PlaceableFoodContext;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Shadow
    private int foodLevel;

    @Shadow
    private float saturationLevel;

    @Inject(method = "eat(IF)V", at = @At("RETURN"))
    private void overatingplus$afterPlaceableFoodEaten(int nutrition, float saturationModifier, CallbackInfo ci) {
        applyPlaceableFoodGainFromContext(nutrition, saturationModifier);
    }

    @Inject(method = "eat(Lnet/minecraft/world/food/FoodProperties;)V", at = @At("RETURN"))
    private void overatingplus$afterPlaceableFoodEatenFromProperties(FoodProperties food, CallbackInfo ci) {
        if (food == null) {
            return;
        }
        applyPlaceableFoodGainFromContext(food.nutrition(), food.saturation());
    }

    private static void applyPlaceableFoodGainFromContext(int nutrition, float saturationModifier) {
        PlaceableFoodContext.Context context = PlaceableFoodContext.take();
        if (context != null) {
            OversaturationFoodLogic.applyPlaceableFoodGain(
                    context.player(),
                    context.block(),
                    nutrition,
                    saturationModifier
            );
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void overatingplus$restoreFullHungerWhenOversaturated(Player player, CallbackInfo ci) {
        if (player == null) {
            return;
        }
        if (OversaturationAttachments.get(player).getPoints() > 0f) {
            this.foodLevel = 20;
            this.saturationLevel = 20.0f;
        }
    }
}
