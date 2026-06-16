package com.overatingplus.logic;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.registry.ModTags;
import com.overatingplus.util.FoodAccumulationSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class OversaturationFoodLogic {
    private OversaturationFoodLogic() {
    }

    public static boolean isOversaturationFood(ItemStack stack) {
        if (stack.isEmpty() || !stack.has(DataComponents.FOOD)) {
            return false;
        }
        // If Farmer's Delight is loaded, minecraft:pumpkin_pie is a block, not an edible item
        if (com.overatingplus.compat.ModCompat.isFarmersDelightLoaded()) {
            ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (itemId.equals(net.minecraft.resources.ResourceLocation.withDefaultNamespace("pumpkin_pie"))) {
                return false;
            }
        }
        return !stack.is(ModTags.NOT_OVERSATURATION_FOOD);
    }

    public static boolean shouldBlockRecentFood(Player player, ResourceLocation foodId) {
        if (!OversaturationLogic.isInOversaturationMode(player)) {
            return false;
        }
        ResourceLocation varietyId = canonicalFoodId(foodId);
        if (varietyId == null) {
            return false;
        }
        ItemStack stack = probeStackForVariety(varietyId);
        if (!FoodVarietyLogic.shouldTrack(varietyId, stack)) {
            return false;
        }
        return OversaturationAttachments.get(player).hasRecentFood(varietyId);
    }

    public static void notifyWantSomethingElse(Player player) {
        player.displayClientMessage(Component.translatable("message.overatingplus.want_something_else"), true);
    }

    public static void applyFoodGain(Player player, ItemStack stack) {
        if (!isOversaturationFood(stack) || !OversaturationLogic.canGainOversaturation(player)) {
            return;
        }
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null) {
            return;
        }
        applyGain(player, food, canonicalFoodId(BuiltInRegistries.ITEM.getKey(stack.getItem())), stack);
    }

    public static void applyPlaceableFoodGain(Player player, Block block, int nutrition, float saturationModifier) {
        if (!OversaturationLogic.canGainOversaturation(player)) {
            return;
        }
        ItemStack stack = PlaceableFoodLogic.getEdibleItem(block);
        if (stack.isEmpty()) {
            return;
        }
        float gain = computePlaceableFoodGain(stack, nutrition, saturationModifier);
        if (gain <= 0f) {
            return;
        }
        applyGain(player, gain, PlaceableFoodLogic.getFoodId(block), stack);
    }

    private static float computePlaceableFoodGain(ItemStack stack, int nutrition, float saturationModifier) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) {
            return OversaturationLogic.computeFoodGain(food);
        }
        if (ModConfig.ACCUMULATION_SOURCE.get() == FoodAccumulationSource.SATURATION) {
            return nutrition * saturationModifier * 2.0f;
        }
        return nutrition;
    }

    private static void applyGain(Player player, FoodProperties food, ResourceLocation foodId, ItemStack stack) {
        applyGain(player, OversaturationLogic.computeFoodGain(food), foodId, stack);
    }

    private static void applyGain(Player player, float gain, ResourceLocation foodId, ItemStack stack) {
        OversaturationData data = OversaturationAttachments.get(player);
        if (gain <= 0f) {
            return;
        }
        ResourceLocation varietyId = canonicalFoodId(foodId);
        int previousStack = OversaturationLogic.getLevel(data.getPoints());
        data.setPoints(OversaturationLogic.applyPointGain(data.getPoints(), gain));
        if (FoodVarietyLogic.shouldTrack(varietyId, stack)) {
            data.addRecentFood(varietyId);
        }
        int stackLevel = OversaturationLogic.getLevel(data.getPoints());
        OversaturationLogic.updateFatArmor(player, data, stackLevel, previousStack);
        OversaturationLogic.clampFatArmorToStack(data, stackLevel);
        OversaturationAttachments.set(player, data);
        OversaturationLogic.syncPlayerState(player);
    }

    public static ResourceLocation canonicalFoodId(ResourceLocation foodId) {
        return PlaceableFoodLogic.getCanonicalFoodId(foodId);
    }

    public static ResourceLocation canonicalFoodId(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return canonicalFoodId(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    /**
     * Hotbar cooldown vignette height: oldest queued food is smallest, newest is largest.
     */
    public static float getRecentFoodOverlayProgress(OversaturationData data, ResourceLocation foodId) {
        ResourceLocation varietyId = canonicalFoodId(foodId);
        return varietyId == null ? 0f : data.getRecentFoodOverlayProgress(varietyId);
    }

    private static ItemStack probeStackForVariety(ResourceLocation varietyId) {
        return PlaceableFoodLogic.resolveProbeStack(varietyId);
    }
}
