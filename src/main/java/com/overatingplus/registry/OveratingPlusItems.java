package com.overatingplus.registry;

import com.overatingplus.OveratingPlusMod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OveratingPlusItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(OveratingPlusMod.MOD_ID);

    public static final DeferredItem<BlockItem> SCALES = ITEMS.register(
            "scales",
            () -> new BlockItem(OveratingPlusBlocks.SCALES.get(), new Item.Properties())
    );

    private OveratingPlusItems() {
    }
}
