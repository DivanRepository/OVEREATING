package com.overatingplus.client;

import com.overatingplus.registry.OveratingPlusBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class ScalesClientState {
    private static final float ANIMATION_SPEED = 0.14f;

    private static boolean onScales;
    private static boolean lookingAtScales;
    private static float panelProgress;

    private ScalesClientState() {
    }

    public static void update(LocalPlayer player) {
        if (player == null) {
            onScales = false;
            lookingAtScales = false;
            return;
        }
        BlockPos feet = BlockPos.containing(player.getX(), player.getY() - 0.05D, player.getZ());
        BlockState state = player.level().getBlockState(feet);
        onScales = state.is(OveratingPlusBlocks.SCALES);

        lookingAtScales = false;
        if (onScales) {
            HitResult hitResult = Minecraft.getInstance().hitResult;
            if (hitResult instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(feet)) {
                lookingAtScales = player.level().getBlockState(feet).is(OveratingPlusBlocks.SCALES);
            }
        }
    }

    public static void tickAnimation() {
        float target = shouldShowPanel() ? 1.0f : 0.0f;
        if (panelProgress < target) {
            panelProgress = Math.min(1.0f, panelProgress + ANIMATION_SPEED);
        } else if (panelProgress > target) {
            panelProgress = Math.max(0.0f, panelProgress - ANIMATION_SPEED);
        }
    }

    public static boolean shouldShowPanel() {
        return onScales && lookingAtScales;
    }

    public static boolean isPanelVisible() {
        return panelProgress > 0.001f;
    }

    public static float getPanelEase() {
        return easeInOutCubic(panelProgress);
    }

    private static float easeInOutCubic(float t) {
        if (t <= 0.0f) {
            return 0.0f;
        }
        if (t >= 1.0f) {
            return 1.0f;
        }
        return t < 0.5f
                ? 4.0f * t * t * t
                : 1.0f - (float) Math.pow(-2.0f * t + 2.0f, 3.0) / 2.0f;
    }

    public static int getAnimatedPanelX(int panelWidth) {
        return Mth.floor(Mth.lerp(getPanelEase(), -panelWidth, 8.0f));
    }
}
