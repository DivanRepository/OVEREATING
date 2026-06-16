package com.overatingplus;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.command.ExtraWeightCommand;
import com.overatingplus.event.OversaturationEvents;
import com.overatingplus.event.PlaceableFoodEvents;
import com.overatingplus.network.OveratingPlusNetworking;
import com.overatingplus.registry.OveratingPlusBlockEntities;
import com.overatingplus.registry.OveratingPlusBlocks;
import com.overatingplus.registry.OveratingPlusItems;
import com.overatingplus.registry.OveratingPlusSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(OveratingPlusMod.MOD_ID)
public class OveratingPlusMod {
    public static final String MOD_ID = "overatingplus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OveratingPlusMod(IEventBus modEventBus, ModContainer modContainer) {
        OversaturationAttachments.ATTACHMENT_TYPES.register(modEventBus);
        OveratingPlusBlocks.BLOCKS.register(modEventBus);
        OveratingPlusBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        OveratingPlusItems.ITEMS.register(modEventBus);
        OveratingPlusSounds.SOUND_EVENTS.register(modEventBus);

        modEventBus.addListener(OveratingPlusNetworking::register);

        NeoForge.EVENT_BUS.register(OversaturationEvents.class);
        NeoForge.EVENT_BUS.register(PlaceableFoodEvents.class);
        NeoForge.EVENT_BUS.addListener(ExtraWeightCommand::register);

        modEventBus.addListener((net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.REDSTONE_BLOCKS) {
                event.accept(OveratingPlusItems.SCALES);
            }
        });

        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, ModConfig.SPEC);
    }
}
