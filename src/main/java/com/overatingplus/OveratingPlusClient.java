package com.overatingplus;

import com.overatingplus.client.OveratingPlusKeyMappings;
import com.overatingplus.client.OversaturationHudLayers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = OveratingPlusMod.MOD_ID, dist = Dist.CLIENT)
public class OveratingPlusClient {
    public OveratingPlusClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(OversaturationHudLayers::register);
        modEventBus.addListener(OveratingPlusKeyMappings::register);
        NeoForge.EVENT_BUS.addListener(OversaturationHudLayers::onRenderGuiLayerPre);
        NeoForge.EVENT_BUS.addListener(OversaturationHudLayers::onRenderGuiLayerPost);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
