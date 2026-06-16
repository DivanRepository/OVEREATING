package com.overatingplus.attachment;

import com.overatingplus.OveratingPlusMod;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class OversaturationAttachments {
    private static final OversaturationData UNINITIALIZED = new OversaturationData();

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, OveratingPlusMod.MOD_ID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<AttachmentType<?>, AttachmentType<OversaturationData>> OVERSATURATION =
            ATTACHMENT_TYPES.register("oversaturation", () -> AttachmentType.builder(OversaturationData::new)
                    .serialize(OversaturationData.CODEC)
                    .sync(OversaturationData.STREAM_CODEC)
                    .build());

    private OversaturationAttachments() {
    }

    public static boolean canAccess(net.minecraft.world.entity.player.Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.connection != null;
        }
        return true;
    }

    public static OversaturationData get(net.minecraft.world.entity.player.Player player) {
        if (!canAccess(player)) {
            return UNINITIALIZED;
        }
        return player.getData(OVERSATURATION.get());
    }

    public static void set(net.minecraft.world.entity.player.Player player, OversaturationData data) {
        if (!canAccess(player)) {
            return;
        }
        player.setData(OVERSATURATION.get(), data);
    }
}
