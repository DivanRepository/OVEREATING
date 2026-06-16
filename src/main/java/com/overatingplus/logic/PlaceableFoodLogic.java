package com.overatingplus.logic;

import com.overatingplus.OveratingPlusMod;
import com.overatingplus.registry.ModTags;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public final class PlaceableFoodLogic {
    private static final String SLICE_SUFFIX = "_slice";
    private static final String FOOD_VARIETY_GROUPS_PATH = "food_variety_groups";
    
    // Cache for canonical IDs loaded from tags
    private static final Map<ResourceLocation, ResourceLocation> canonicalIdCache = new HashMap<>();

    private PlaceableFoodLogic() {
    }

    /**
     * Gets the canonical food ID for a given item/block ID.
     * First checks food_variety_groups tags, then falls back to slice suffix removal.
     * 
     * @param foodId the original food ID
     * @return the canonical food ID for variety tracking
     */
    public static ResourceLocation getCanonicalFoodId(ResourceLocation foodId) {
        // Check cache first
        ResourceLocation cached = canonicalIdCache.get(foodId);
        if (cached != null) {
            return cached;
        }
        
        // Check food_variety_groups tags
        ResourceLocation fromTag = findCanonicalIdFromTags(foodId);
        if (fromTag != null) {
            // Validate that the canonical ID exists in registries
            if (!BuiltInRegistries.ITEM.containsKey(fromTag) && !BuiltInRegistries.BLOCK.containsKey(fromTag)) {
                OveratingPlusMod.LOGGER.warn("Food variety group canonical ID '{}' does not exist in item or block registry", fromTag);
            }
            canonicalIdCache.put(foodId, fromTag);
            return fromTag;
        }
        
        // Fallback: remove _slice suffix
        ResourceLocation fallback = canonicalVarietyId(foodId);
        canonicalIdCache.put(foodId, fallback);
        return fallback;
    }

    /**
     * Finds the canonical ID for a food item by checking food_variety_groups tags.
     * 
     * @param foodId the food ID to look up
     * @return the canonical ID if found in a tag, null otherwise
     */
    private static ResourceLocation findCanonicalIdFromTags(ResourceLocation foodId) {
        // Iterate through all item tags to find food_variety_groups
        for (TagKey<Item> tagKey : BuiltInRegistries.ITEM.getTagNames().toList()) {
            ResourceLocation tagId = tagKey.location();
            
            // Check if this is a food_variety_groups tag (path format: "food_variety_groups/namespace/path")
            if (tagId.getNamespace().equals(OveratingPlusMod.MOD_ID)
                    && tagId.getPath().startsWith(FOOD_VARIETY_GROUPS_PATH + "/")) {
                
                // Get the canonical ID from the tag path
                // Path format: "food_variety_groups/namespace/path" -> canonical ID: "namespace:path"
                String path = tagId.getPath();
                String afterPrefix = path.substring(FOOD_VARIETY_GROUPS_PATH.length() + 1);
                
                // Find the first "/" to separate namespace from path
                int firstSlash = afterPrefix.indexOf('/');
                if (firstSlash < 0) {
                    continue; // Invalid format, skip
                }
                
                String namespace = afterPrefix.substring(0, firstSlash);
                String itemPath = afterPrefix.substring(firstSlash + 1);
                ResourceLocation canonicalId = ResourceLocation.fromNamespaceAndPath(namespace, itemPath);
                
                // Check if the foodId is in this tag
                var itemHolder = BuiltInRegistries.ITEM.getHolder(foodId);
                if (itemHolder.isEmpty()) {
                    continue; // Item not found in registry, skip
                }
                var tagHolder = BuiltInRegistries.ITEM.getTag(tagKey);
                if (tagHolder.isPresent()) {
                    var tag = tagHolder.get();
                    if (tag.contains(itemHolder.get())) {
                        return canonicalId;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isPlaceableFood(BlockState state) {
        return isPlaceableFood(state.getBlock());
    }

    public static boolean isPlaceableFood(Block block) {
        if (BuiltInRegistries.BLOCK.wrapAsHolder(block).is(ModTags.PLACEABLE_OVERSATURATION_BLOCKS)) {
            return true;
        }
        if (isKnownPlaceableFoodBlock(BuiltInRegistries.BLOCK.getKey(block))) {
            return true;
        }
        ItemStack stack = getRepresentativeItem(block);
        return !stack.isEmpty() && stack.is(ModTags.PLACEABLE_OVERSATURATION_FOOD);
    }

    public static ItemStack getRepresentativeItem(Block block) {
        return new ItemStack(block.asItem());
    }

    public static ItemStack getEdibleItem(Block block) {
        ItemStack blockItem = getRepresentativeItem(block);
        if (blockItem.has(DataComponents.FOOD)) {
            return blockItem;
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        Item sliceItem = BuiltInRegistries.ITEM.get(sliceIdForBlock(blockId));
        if (sliceItem != null) {
            ItemStack slice = new ItemStack(sliceItem);
            if (slice.has(DataComponents.FOOD)) {
                return slice;
            }
        }
        return blockItem;
    }

    public static ResourceLocation getFoodId(Block block) {
        return getCanonicalFoodId(BuiltInRegistries.BLOCK.getKey(block));
    }

    public static ItemStack resolveProbeStack(ResourceLocation foodId) {
        Item item = BuiltInRegistries.ITEM.get(foodId);
        if (item != null) {
            return new ItemStack(item);
        }

        Block block = BuiltInRegistries.BLOCK.get(foodId);
        if (block != null) {
            ItemStack edible = getEdibleItem(block);
            if (!edible.isEmpty()) {
                return edible;
            }
            ItemStack blockItem = getRepresentativeItem(block);
            if (!blockItem.isEmpty()) {
                return blockItem;
            }
        }

        Item sliceItem = BuiltInRegistries.ITEM.get(sliceIdForBlock(foodId));
        if (sliceItem != null) {
            return new ItemStack(sliceItem);
        }

        ResourceLocation canonicalId = canonicalVarietyId(foodId);
        if (!canonicalId.equals(foodId)) {
            return resolveProbeStack(canonicalId);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Removes the _slice suffix from a food ID for basic canonicalization.
     */
    public static ResourceLocation canonicalVarietyId(ResourceLocation foodId) {
        String path = foodId.getPath();
        if (path.endsWith(SLICE_SUFFIX)) {
            return ResourceLocation.fromNamespaceAndPath(
                    foodId.getNamespace(),
                    path.substring(0, path.length() - SLICE_SUFFIX.length())
            );
        }
        return foodId;
    }

    private static ResourceLocation sliceIdForBlock(ResourceLocation blockId) {
        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), blockId.getPath() + SLICE_SUFFIX);
    }

    private static boolean isKnownPlaceableFoodBlock(ResourceLocation id) {
        if ("minecraft".equals(id.getNamespace()) && "cake".equals(id.getPath())) {
            return true;
        }
        if (!"farmersdelight".equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return path.endsWith("_pie") || path.contains("cheesecake");
    }
}
