package com.overatingplus.client;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class HudHeartLayout {
    private static final int HEARTS_PER_ROW = 10;

    private HudHeartLayout() {
    }

    public static int getHealthHeartCount(Player player) {
        return Mth.ceil(player.getMaxHealth() / 2.0f);
    }

    public static int getAbsorptionHeartCount(Player player) {
        if (player.getAbsorptionAmount() <= 0f) {
            return 0;
        }
        return Mth.ceil(player.getAbsorptionAmount() / 2.0f);
    }

    public static int getVanillaRowsAboveHealth(Player player) {
        int totalHearts = getHealthHeartCount(player) + getAbsorptionHeartCount(player);
        if (totalHearts <= HEARTS_PER_ROW) {
            return 0;
        }
        return Mth.positiveCeilDiv(totalHearts, HEARTS_PER_ROW) - 1;
    }

    public static HeartSlot getFatHeartSlot(int fatHeartIndex) {
        return new HeartSlot(fatHeartIndex % HEARTS_PER_ROW, fatHeartIndex / HEARTS_PER_ROW);
    }

    public static boolean shouldRelocateAbsorption(Player player, int maxFatHeartCount) {
        return getAbsorptionHeartCount(player) > 0 && maxFatHeartCount > getHealthHeartCount(player);
    }

    public static int getAbsorptionStartRow(int maxFatHeartCount) {
        return Mth.positiveCeilDiv(maxFatHeartCount, HEARTS_PER_ROW);
    }

    public static HeartSlot getAbsorptionHeartSlot(int absorptionHeartIndex, int maxFatHeartCount) {
        int slot = maxFatHeartCount + absorptionHeartIndex;
        return new HeartSlot(slot % HEARTS_PER_ROW, slot / HEARTS_PER_ROW);
    }

    public static int getModTopRow(Player player, int maxFatHeartCount) {
        int fatTopRow = Mth.positiveCeilDiv(maxFatHeartCount, HEARTS_PER_ROW) - 1;
        if (!shouldRelocateAbsorption(player, maxFatHeartCount)) {
            return fatTopRow;
        }
        int absorptionHearts = getAbsorptionHeartCount(player);
        int absStartRow = getAbsorptionStartRow(maxFatHeartCount);
        int absEndRow = absStartRow + Mth.positiveCeilDiv(absorptionHearts, HEARTS_PER_ROW) - 1;
        return Math.max(fatTopRow, absEndRow);
    }

    public record HeartSlot(int column, int row) {
    }
}
