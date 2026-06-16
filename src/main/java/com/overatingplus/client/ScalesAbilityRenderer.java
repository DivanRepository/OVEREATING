package com.overatingplus.client;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class ScalesAbilityRenderer {
    private static final ResourceLocation BOUNCE_OFF = ResourceLocation.fromNamespaceAndPath(
            "overatingplus", "textures/gui/scales/bounce_off.png");
    private static final ResourceLocation BOUNCE_ON = ResourceLocation.fromNamespaceAndPath(
            "overatingplus", "textures/gui/scales/bounce_on.png");
    private static final ResourceLocation SHOCKWAVE_OFF = ResourceLocation.fromNamespaceAndPath(
            "overatingplus", "textures/gui/scales/shockwave_off.png");
    private static final ResourceLocation SHOCKWAVE_ON = ResourceLocation.fromNamespaceAndPath(
            "overatingplus", "textures/gui/scales/shockwave_on.png");

    private ScalesAbilityRenderer() {
    }

    public static void render(GuiGraphics graphics, Minecraft minecraft, Player player, int panelX, int panelY, float alpha) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());

        renderAbility(
                graphics,
                minecraft,
                panelX,
                panelY,
                0,
                OversaturationLogic.hasSlimeBounce(player),
                stacksUntilUnlock(stack, ModConfig.SLIME_BOUNCE_START_LEVEL.get()),
                BOUNCE_ON,
                BOUNCE_OFF,
                alpha
        );
        renderAbility(
                graphics,
                minecraft,
                panelX,
                panelY,
                1,
                OversaturationLogic.hasShockwave(player),
                stacksUntilUnlock(stack, ModConfig.SHOCKWAVE_START_LEVEL.get()),
                SHOCKWAVE_ON,
                SHOCKWAVE_OFF,
                alpha
        );
    }

    private static void renderAbility(
            GuiGraphics graphics,
            Minecraft minecraft,
            int panelX,
            int panelY,
            int slotIndex,
            boolean unlocked,
            int stacksRemaining,
            ResourceLocation onTexture,
            ResourceLocation offTexture,
            float alpha
    ) {
        int slotX = ScalesPanelLayout.getAbilitySlotX(slotIndex);
        int slotY = ScalesPanelLayout.ABILITY_ROW_Y;
        ResourceLocation texture = unlocked ? onTexture : offTexture;

        graphics.pose().pushPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.blit(
                texture,
                panelX + slotX,
                panelY + slotY,
                slotX,
                slotY,
                ScalesPanelLayout.ABILITY_SLOT_SIZE,
                ScalesPanelLayout.ABILITY_SLOT_SIZE,
                ScalesPanelLayout.ABILITY_TEXTURE_WIDTH,
                ScalesPanelLayout.ABILITY_TEXTURE_HEIGHT
        );
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();

        if (!unlocked && stacksRemaining > 0) {
            drawUnlockCounter(graphics, minecraft, panelX + slotX, panelY + slotY, stacksRemaining, alpha);
        }
    }

    private static void drawUnlockCounter(
            GuiGraphics graphics,
            Minecraft minecraft,
            int slotX,
            int slotY,
            int stacksRemaining,
            float alpha
    ) {
        String text = String.valueOf(stacksRemaining);
        int textWidth = minecraft.font.width(text);
        int textX = slotX + ScalesPanelLayout.ABILITY_SLOT_SIZE - textWidth - 1;
        int textY = slotY + 1;
        int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;
        graphics.drawString(minecraft.font, text, textX, textY, color, true);
    }

    private static int stacksUntilUnlock(int currentStack, int requiredStack) {
        return Math.max(0, requiredStack - currentStack);
    }
}
