package com.overatingplus.client;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.logic.OversaturationFoodLogic;
import com.overatingplus.logic.OversaturationLogic;
import com.overatingplus.logic.PlaceableFoodLogic;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class OversaturationFoodCooldownRenderer {
    private static final int SLOT_SIZE = 16;
    private static final int OVERLAY_COLOR = 0xA0FFFFFF;

    private OversaturationFoodCooldownRenderer() {
    }

    public static boolean shouldRender(LocalPlayer player) {
        return ModConfig.ENABLE_FOOD_VARIETY.get()
                && OversaturationLogic.isInOversaturationMode(player)
                && !OversaturationAttachments.get(player).recentFoodsList().isEmpty();
    }

    public static void renderSlot(GuiGraphics graphics, LocalPlayer player, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }
        OversaturationData data = OversaturationAttachments.get(player);

        // Get the item ID and get the canonical food ID for variety checks
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        ResourceLocation foodId = PlaceableFoodLogic.getCanonicalFoodId(itemId);

        // If Farmer's Delight is loaded and this is minecraft:pumpkin_pie,
        // use farmersdelight:pumpkin_pie as canonical ID for variety checks
        if (com.overatingplus.compat.ModCompat.isFarmersDelightLoaded()
                && itemId.equals(net.minecraft.resources.ResourceLocation.withDefaultNamespace("pumpkin_pie"))) {
            foodId = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("farmersdelight", "pumpkin_pie");
        }

        if (foodId == null) {
            return;
        }
        float progress = OversaturationFoodLogic.getRecentFoodOverlayProgress(data, foodId);
        if (progress <= 0f) {
            return;
        }
        int overlayHeight = Math.max(1, Math.round(SLOT_SIZE * progress));
        graphics.fill(x, y + SLOT_SIZE - overlayHeight, x + SLOT_SIZE, y + SLOT_SIZE, OVERLAY_COLOR);
    }
}
