package com.overatingplus.client;

import com.overatingplus.OveratingPlusMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = OveratingPlusMod.MOD_ID, value = Dist.CLIENT)
public final class OversaturationFoodCooldownOverlay {
    private static final int HOTBAR_LEFT_OFFSET = 91;
    private static final int HOTBAR_TOP_OFFSET = 22;
    private static final int SLOT_STEP = 20;
    private static final int SLOT_INSET = 3;
    private static final int SLOT_SIZE = 16;

    private OversaturationFoodCooldownOverlay() {
    }

    @SubscribeEvent
    public static void onRenderHotbar(RenderGuiLayerEvent.Post event) {
        if (!VanillaGuiLayers.HOTBAR.equals(event.getName())) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || player.isSpectator()) {
            return;
        }
        if (!OversaturationFoodCooldownRenderer.shouldRender(player)) {
            return;
        }

        var graphics = event.getGuiGraphics();
        int left = graphics.guiWidth() / 2 - HOTBAR_LEFT_OFFSET;
        int top = graphics.guiHeight() - HOTBAR_TOP_OFFSET;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            int x = left + slot * SLOT_STEP + SLOT_INSET;
            int y = top + SLOT_INSET;
            OversaturationFoodCooldownRenderer.renderSlot(graphics, player, stack, x, y);
        }
    }

    @SubscribeEvent
    public static void onRenderContainerForeground(ContainerScreenEvent.Render.Foreground event) {
        renderContainerSlots(event.getContainerScreen(), event.getGuiGraphics());
    }

    private static void renderContainerSlots(AbstractContainerScreen<?> screen, net.minecraft.client.gui.GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !OversaturationFoodCooldownRenderer.shouldRender(player)) {
            return;
        }

        for (Slot slot : screen.getMenu().slots) {
            if (!slot.isActive() || !isSlotWithinPanel(screen, slot)) {
                continue;
            }
            OversaturationFoodCooldownRenderer.renderSlot(
                    graphics,
                    player,
                    slot.getItem(),
                    slot.x,
                    slot.y
            );
        }
    }

    private static boolean isSlotWithinPanel(AbstractContainerScreen<?> screen, Slot slot) {
        return slot.x >= -SLOT_SIZE
                && slot.x < screen.getXSize() + SLOT_SIZE
                && slot.y >= -SLOT_SIZE
                && slot.y < screen.getYSize() + SLOT_SIZE;
    }
}
