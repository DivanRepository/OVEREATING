package com.overatingplus.client;

import com.overatingplus.OveratingPlusMod;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = OveratingPlusMod.MOD_ID, value = Dist.CLIENT)
public final class OversaturationClientEvents {
    private OversaturationClientEvents() {
    }

    @SubscribeEvent
    public static void onClientPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player) || !player.level().isClientSide()) {
            return;
        }
        OversaturationData data = OversaturationAttachments.get(player);
        int stack = OversaturationLogic.getLevel(data.getPoints());
        OversaturationLogic.updatePlayerSize(player, data, stack);
        ScalesClientState.update(player);
        ScalesClientState.tickAnimation();
    }
}
