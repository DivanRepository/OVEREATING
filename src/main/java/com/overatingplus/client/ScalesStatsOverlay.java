package com.overatingplus.client;

import com.overatingplus.util.OversaturationStatsFormatter;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class ScalesStatsOverlay {
    private static final ResourceLocation PANEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            "overatingplus", "textures/gui/scales_gui.png");

    private ScalesStatsOverlay() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        if (!ScalesClientState.isPanelVisible()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }

        float ease = ScalesClientState.getPanelEase();
        int alpha = Mth.clamp(Mth.floor(ease * 255.0f), 0, 255);
        int x = ScalesClientState.getAnimatedPanelX(ScalesPanelLayout.PANEL_WIDTH);
        int y = (graphics.guiHeight() - ScalesPanelLayout.PANEL_HEIGHT) / 2;

        graphics.pose().pushPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, ease);
        graphics.blit(
                PANEL_TEXTURE,
                x,
                y,
                0,
                0,
                ScalesPanelLayout.PANEL_WIDTH,
                ScalesPanelLayout.PANEL_HEIGHT,
                ScalesPanelLayout.PANEL_WIDTH,
                ScalesPanelLayout.PANEL_HEIGHT
        );
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();

        int textColor = (alpha << 24) | (ScalesPanelLayout.TEXT_COLOR & 0x00FFFFFF);
        drawStat(graphics, minecraft, x, y, 0, OversaturationStatsFormatter.formatScalesWeight(player), textColor);
        drawStat(graphics, minecraft, x, y, 1, OversaturationStatsFormatter.formatScalesFatArmor(player), textColor);
        drawStat(graphics, minecraft, x, y, 2, OversaturationStatsFormatter.formatScalesWidth(player), textColor);
        drawStat(graphics, minecraft, x, y, 3, OversaturationStatsFormatter.formatScalesStrength(player), textColor);
        drawStat(graphics, minecraft, x, y, 4, OversaturationStatsFormatter.formatScalesKnockbackResist(player), textColor);
        drawStat(graphics, minecraft, x, y, 5, OversaturationStatsFormatter.formatScalesSlowdown(player), textColor);
        ScalesAbilityRenderer.render(graphics, minecraft, player, x, y, ease);
    }

    private static void drawStat(
            GuiGraphics graphics,
            Minecraft minecraft,
            int panelX,
            int panelY,
            int row,
            String text,
            int color
    ) {
        graphics.drawString(
                minecraft.font,
                text,
                panelX + ScalesPanelLayout.TEXT_X,
                panelY + ScalesPanelLayout.getRowTextY(row),
                color,
                true
        );
    }
}
