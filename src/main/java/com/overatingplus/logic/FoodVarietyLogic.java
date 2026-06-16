package com.overatingplus.logic;

import com.overatingplus.ModConfig;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class FoodVarietyLogic {
    private FoodVarietyLogic() {
    }

    public static boolean shouldTrack(ResourceLocation foodId, ItemStack stack) {
        if (!ModConfig.ENABLE_FOOD_VARIETY.get()) {
            return false;
        }
        if (matchesAny(ModConfig.FOOD_VARIETY_BLACKLIST.get(), foodId, stack)) {
            return false;
        }
        List<? extends String> whitelist = ModConfig.FOOD_VARIETY_WHITELIST.get();
        if (whitelist.isEmpty()) {
            return OversaturationFoodLogic.isOversaturationFood(stack) || isPlaceableFoodItem(foodId);
        }
        return matchesAny(whitelist, foodId, stack);
    }

    private static boolean isPlaceableFoodItem(ResourceLocation foodId) {
        Block block = BuiltInRegistries.BLOCK.get(foodId);
        if (block != null && PlaceableFoodLogic.isPlaceableFood(block)) {
            return true;
        }
        Item item = BuiltInRegistries.ITEM.get(foodId);
        if (item != null) {
            Block itemBlock = Block.byItem(item);
            if (itemBlock != null && PlaceableFoodLogic.isPlaceableFood(itemBlock)) {
                return true;
            }
        }
        ResourceLocation canonicalId = PlaceableFoodLogic.canonicalVarietyId(foodId);
        if (canonicalId.equals(foodId)) {
            return false;
        }
        return isPlaceableFoodItem(canonicalId);
    }

    private static boolean matchesAny(List<? extends String> entries, ResourceLocation foodId, ItemStack stack) {
        if (entries.isEmpty()) {
            return false;
        }
        ItemStack probe = stack.isEmpty() ? PlaceableFoodLogic.resolveProbeStack(foodId) : stack;
        for (String entry : entries) {
            if (entry.isEmpty()) {
                continue;
            }
            if (entry.startsWith("#")) {
                ResourceLocation tagId = ResourceLocation.parse(entry.substring(1));
                TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
                if (!probe.isEmpty() && probe.is(tag)) {
                    return true;
                }
            } else if (foodId.equals(ResourceLocation.parse(entry))) {
                return true;
            }
        }
        return false;
    }
}
