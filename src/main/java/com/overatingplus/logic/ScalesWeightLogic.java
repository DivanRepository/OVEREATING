package com.overatingplus.logic;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class ScalesWeightLogic {
    public static final int TEXTURE_LEVEL_COUNT = 8;

    private ScalesWeightLogic() {
    }

    private static boolean isClient() {
        return net.minecraft.client.Minecraft.getInstance().level != null;
    }

    public static float getDisplayWeight(Player player) {
        return getDisplayWeight(OversaturationAttachments.get(player).getPoints());
    }

    public static float getDisplayWeight(float points) {
        int perLevel = OversaturationLogic.getPointsPerLevel();
        if (perLevel <= 0 || points <= 0f) {
            return 0f;
        }
        int stack = OversaturationLogic.getLevel(points);
        float remainder = points % perLevel;
        float fraction = remainder == 0f ? 0f : remainder / perLevel;
        return stack + fraction;
    }

    public static int getTextureLevel(Player player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        return mapStackToLevel(stack);
    }

    public static int mapStackToLevel(int stack) {
        if (stack <= 0) {
            return 0;
        }
        int minStack = isClient() ? OversaturationLogic.CLIENT_SCALES_REDSTONE_MIN_STACK : ModConfig.SCALES_REDSTONE_MIN_STACK.get();
        if (stack < minStack) {
            return 0;
        }
        int maxStack = OversaturationLogic.getMaxStacks();
        if (maxStack <= minStack) {
            return TEXTURE_LEVEL_COUNT - 1;
        }
        float progress = (stack - minStack) / (float) (maxStack - minStack);
        return Mth.clamp(Mth.floor(progress * (TEXTURE_LEVEL_COUNT - 1)), 0, TEXTURE_LEVEL_COUNT - 1);
    }
}
