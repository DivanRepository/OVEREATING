package com.overatingplus.network;

import com.overatingplus.OveratingPlusMod;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class OveratingPlusNetworking {
    private OveratingPlusNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(OveratingPlusMod.MOD_ID);
        registrar.playToServer(
                ToggleBouncePayload.TYPE,
                ToggleBouncePayload.STREAM_CODEC,
                ToggleBouncePayload::handle
        );
        registrar.playToServer(
                ToggleShockwavePayload.TYPE,
                ToggleShockwavePayload.STREAM_CODEC,
                ToggleShockwavePayload::handle
        );
        registrar.playToClient(
                ConfigSyncPayload.TYPE,
                ConfigSyncPayload.STREAM_CODEC,
                ConfigSyncPayload::handle
        );
    }

    public static void sendConfigSync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, ConfigSyncPayload.fromServerConfig());
    }
}
