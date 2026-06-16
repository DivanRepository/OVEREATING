package com.overatingplus.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.overatingplus.logic.OversaturationSizeLogic;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private Entity entity;

    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float overatingplus$pullBackThirdPerson(float distance) {
        if (entity instanceof Player player) {
            return distance + OversaturationSizeLogic.getCameraPullback(player);
        }
        return distance;
    }
}
