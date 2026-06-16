package com.overatingplus.registry;

import com.overatingplus.OveratingPlusMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    public static final TagKey<Item> NOT_OVERSATURATION_FOOD = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "not_oversaturation_food")
    );

    public static final TagKey<Item> PLACEABLE_OVERSATURATION_FOOD = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "placeable_oversaturation_food")
    );

    public static final TagKey<Block> PLACEABLE_OVERSATURATION_BLOCKS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "placeable_oversaturation_blocks")
    );

    private ModTags() {
    }
}
