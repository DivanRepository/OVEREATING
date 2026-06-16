package com.overatingplus.registry;

import com.overatingplus.OveratingPlusMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OveratingPlusSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, OveratingPlusMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SCALES_WEIGH = SOUND_EVENTS.register(
            "scales_weigh",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "scales_weigh"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> SHOCKWAVE = SOUND_EVENTS.register(
            "shockwave",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "shockwave"))
    );

    private OveratingPlusSounds() {
    }
}
