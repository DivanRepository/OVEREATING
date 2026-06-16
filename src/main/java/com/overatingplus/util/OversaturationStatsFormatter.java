package com.overatingplus.util;

import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.logic.OversaturationLogic;
import com.overatingplus.logic.OversaturationSizeLogic;
import com.overatingplus.logic.ScalesWeightLogic;

import net.minecraft.world.entity.player.Player;

public final class OversaturationStatsFormatter {
    private OversaturationStatsFormatter() {
    }

    public static String formatScalesWeight(Player player) {
        return "+" + String.format("%.1f", ScalesWeightLogic.getDisplayWeight(player));
    }

    public static String formatScalesFatArmor(Player player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        float fatMax = OversaturationLogic.getMaxFatArmorHp(stack);
        return "+" + String.format("%.1f", fatMax);
    }

    public static String formatScalesWidth(Player player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        return String.format("%.1f", OversaturationSizeLogic.getTargetWidth(stack));
    }

    public static String formatScalesStrength(Player player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        return "+" + formatPercent(OversaturationLogic.getStrengthPercent(stack)) + "%";
    }

    public static String formatScalesKnockbackResist(Player player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        return "+" + formatPercent(OversaturationLogic.getKnockbackResistanceBonus(stack)) + "%";
    }

    public static String formatScalesSlowdown(Player player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        return "-" + formatPercent(OversaturationLogic.getSlowdownPercent(stack)) + "%";
    }

    private static String formatPercent(float value) {
        return String.format("%.1f", value * 100f);
    }
}
