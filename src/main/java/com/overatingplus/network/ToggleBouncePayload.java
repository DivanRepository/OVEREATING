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

public record ToggleBouncePayload() implements CustomPacketPayload {
    public static final Type<ToggleBouncePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "toggle_bounce"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleBouncePayload> STREAM_CODEC =
            StreamCodec.unit(new ToggleBouncePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleBouncePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!OversaturationLogic.hasSlimeBounce(player)) {
                int remaining = stacksUntilUnlock(player, ModConfig.SLIME_BOUNCE_START_LEVEL.get());
                player.displayClientMessage(Component.translatable(
                        "message.overatingplus.bounce_locked",
                        remaining
                ), true);
                return;
            }
            OversaturationData data = OversaturationAttachments.get(player);
            data.setBounceEnabled(!data.isBounceEnabled());
            OversaturationAttachments.set(player, data);
            player.syncData(OversaturationAttachments.OVERSATURATION.get());
            Component message = data.isBounceEnabled()
                    ? Component.translatable("message.overatingplus.bounce_on")
                    : Component.translatable("message.overatingplus.bounce_off");
            player.displayClientMessage(message, true);
        });
    }

    private static int stacksUntilUnlock(ServerPlayer player, int requiredStack) {
        int current = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        return Math.max(0, requiredStack - current);
    }
}
