package com.overatingplus.client;

import com.overatingplus.OveratingPlusMod;
import com.overatingplus.logic.OversaturationFoodLogic;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = OveratingPlusMod.MOD_ID, value = Dist.CLIENT)
public final class OversaturationFoodClientEvents {
    private OversaturationFoodClientEvents() {
    }

    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem();
        if (!OversaturationFoodLogic.isOversaturationFood(stack)) {
            return;
        }
        ResourceLocation foodId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!OversaturationFoodLogic.shouldBlockRecentFood(player, foodId)) {
            return;
        }
        event.setCanceled(true);
        player.stopUsingItem();
    }

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem();
        if (!OversaturationFoodLogic.isOversaturationFood(stack)) {
            return;
        }
        ResourceLocation foodId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!OversaturationFoodLogic.shouldBlockRecentFood(player, foodId)) {
            return;
        }
        event.setCanceled(true);
        player.stopUsingItem();
    }
}
