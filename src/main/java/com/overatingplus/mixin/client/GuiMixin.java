package com.overatingplus.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.client.HudHeartLayout;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyVariable(
            method = "renderHearts",
            at = @At("HEAD"),
            ordinal = 6,
            argsOnly = true
    )
    private int overatingplus$hideRelocatedAbsorption(int absorption) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return absorption;
        }
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        float maxFat = OversaturationLogic.getMaxFatArmorHp(stack);
        int maxFatHeartCount = Mth.ceil(maxFat / 2.0f);
        if (HudHeartLayout.shouldRelocateAbsorption(player, maxFatHeartCount)) {
            return 0;
        }
        return absorption;
    }
}
