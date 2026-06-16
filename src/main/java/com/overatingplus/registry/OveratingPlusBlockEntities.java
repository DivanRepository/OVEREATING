package com.overatingplus.registry;

import com.overatingplus.OveratingPlusMod;
import com.overatingplus.block.entity.ScalesBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OveratingPlusBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OveratingPlusMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScalesBlockEntity>> SCALES =
            BLOCK_ENTITIES.register("scales", () -> BlockEntityType.Builder
                    .of(ScalesBlockEntity::new, OveratingPlusBlocks.SCALES.get())
                    .build(null));

    private OveratingPlusBlockEntities() {
    }
}
