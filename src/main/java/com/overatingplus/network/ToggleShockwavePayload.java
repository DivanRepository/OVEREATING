package com.overatingplus.network;

import com.overatingplus.ModConfig;
import com.overatingplus.OveratingPlusMod;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleShockwavePayload() implements CustomPacketPayload {
    public static final Type<ToggleShockwavePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "toggle_shockwave"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleShockwavePayload> STREAM_CODEC =
            StreamCodec.unit(new ToggleShockwavePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleShockwavePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!OversaturationLogic.hasShockwave(player)) {
                int remaining = stacksUntilUnlock(player, ModConfig.SHOCKWAVE_START_LEVEL.get());
                player.displayClientMessage(Component.translatable(
                        "message.overatingplus.shockwave_locked",
                        remaining
                ), true);
                return;
            }
            OversaturationData data = OversaturationAttachments.get(player);
            data.setShockwaveEnabled(!data.isShockwaveEnabled());
            OversaturationAttachments.set(player, data);
            player.syncData(OversaturationAttachments.OVERSATURATION.get());
            Component message = data.isShockwaveEnabled()
                    ? Component.translatable("message.overatingplus.shockwave_on")
                    : Component.translatable("message.overatingplus.shockwave_off");
            player.displayClientMessage(message, true);
        });
    }

    private static int stacksUntilUnlock(ServerPlayer player, int requiredStack) {
        int current = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        return Math.max(0, requiredStack - current);
    }
}
