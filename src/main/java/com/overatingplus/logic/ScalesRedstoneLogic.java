package com.overatingplus.logic;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class ScalesRedstoneLogic {
    private ScalesRedstoneLogic() {
    }

    private static boolean isClient() {
        return net.minecraft.client.Minecraft.getInstance().level != null;
    }

    public static int getRedstonePower(Player player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        int minStack = isClient() ? OversaturationLogic.CLIENT_SCALES_REDSTONE_MIN_STACK : ModConfig.SCALES_REDSTONE_MIN_STACK.get();
        int maxStack = OversaturationLogic.getMaxStacks();
        int maxPower = isClient() ? OversaturationLogic.CLIENT_SCALES_REDSTONE_MAX_POWER : ModConfig.SCALES_REDSTONE_MAX_POWER.get();

        // Always emit at least redstone signal 1 when player has 0 stacks (minimum signal)
        if (stack <= 0) {
            return 1;
        }

        if (stack < minStack) {
            return 0;
        }
        if (maxStack <= minStack) {
            return Mth.clamp(maxPower, 1, 15);
        }
        float progress = (stack - minStack) / (float) (maxStack - minStack);
        // Minimum signal is 1, not 0
        return Mth.clamp(Mth.floor(progress * maxPower), 1, 15);
    }
}
