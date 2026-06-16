package com.overatingplus.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.overatingplus.OveratingPlusMod;
import com.overatingplus.network.ToggleBouncePayload;
import com.overatingplus.network.ToggleShockwavePayload;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = OveratingPlusMod.MOD_ID, value = Dist.CLIENT)
public final class OveratingPlusKeyMappings {
    public static final String CATEGORY = "key.categories.overatingplus";

    public static KeyMapping TOGGLE_BOUNCE;
    public static KeyMapping TOGGLE_SHOCKWAVE;

    private OveratingPlusKeyMappings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        TOGGLE_BOUNCE = new KeyMapping(
                "key.overatingplus.toggle_bounce",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                CATEGORY
        );
        TOGGLE_SHOCKWAVE = new KeyMapping(
                "key.overatingplus.toggle_shockwave",
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                CATEGORY
        );
        event.register(TOGGLE_BOUNCE);
        event.register(TOGGLE_SHOCKWAVE);
    }

    @SubscribeEvent
    public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        if (TOGGLE_BOUNCE != null) {
            while (TOGGLE_BOUNCE.consumeClick()) {
                PacketDistributor.sendToServer(new ToggleBouncePayload());
            }
        }
        if (TOGGLE_SHOCKWAVE != null) {
            while (TOGGLE_SHOCKWAVE.consumeClick()) {
                PacketDistributor.sendToServer(new ToggleShockwavePayload());
            }
        }
    }
}
