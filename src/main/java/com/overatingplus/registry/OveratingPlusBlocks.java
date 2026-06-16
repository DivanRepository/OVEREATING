package com.overatingplus.registry;

import com.overatingplus.OveratingPlusMod;
import com.overatingplus.block.ScalesBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OveratingPlusBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, OveratingPlusMod.MOD_ID);

    public static final DeferredHolder<Block, ScalesBlock> SCALES = BLOCKS.register(
            "scales",
            () -> new ScalesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(0.5F, 3.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion())
    );

    private OveratingPlusBlocks() {
    }
}
