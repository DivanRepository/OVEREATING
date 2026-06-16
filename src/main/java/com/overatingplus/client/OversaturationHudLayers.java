package com.overatingplus.client;

import com.overatingplus.OveratingPlusMod;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class OversaturationHudLayers {
    private static final ResourceLocation COUNTER_LAYER =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "oversaturation_counter");
    private static final ResourceLocation FOOD_FILL_LAYER =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "oversaturation_food_fill");
    private static final ResourceLocation FAT_HEARTS_LAYER =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "fat_armor_hearts");
    private static final ResourceLocation ABSORPTION_HEARTS_LAYER =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "relocated_absorption_hearts");
    private static final ResourceLocation SCALES_PANEL_LAYER =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "scales_panel");

    private static final ResourceLocation FAT_HEART_FULL =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/fat_heart_full.png");
    private static final ResourceLocation FAT_HEART_HALF =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/fat_heart_half.png");
    private static final ResourceLocation FAT_HEART_FULL_BG =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/fat_heart_full_bg.png");
    private static final ResourceLocation FAT_HEART_HALF_BG =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/fat_heart_half_bg.png");

    private static final ResourceLocation BURGER_FULL =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/burger_full.png");
    private static final ResourceLocation BURGER_HALF =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/burger_half.png");
    private static final ResourceLocation BURGER_FULL_BG =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/burger_full_bg.png");
    private static final ResourceLocation FOOD_FULL_HUNGER =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/food_full_hunger.png");
    private static final ResourceLocation FOOD_HALF_HUNGER =
            ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "textures/gui/food_half_hunger.png");
    private static final ResourceLocation ABSORPTION_HEART_FULL =
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full");
    private static final ResourceLocation ABSORPTION_HEART_HALF =
            ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half");

    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 8;
    private static final int FOOD_ICONS = 10;
    private static final int ROW_HEIGHT = 10;
    private static final float COUNTER_SCALE = 1.75f;

    private static int vanillaHudOffset;

    private OversaturationHudLayers() {
    }

    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, FOOD_FILL_LAYER, OversaturationHudLayers::renderFoodLevelFill);
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, COUNTER_LAYER, OversaturationHudLayers::renderCounter);
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, FAT_HEARTS_LAYER, OversaturationHudLayers::renderFatHearts);
        event.registerAbove(FAT_HEARTS_LAYER, ABSORPTION_HEARTS_LAYER, OversaturationHudLayers::renderRelocatedAbsorption);
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, SCALES_PANEL_LAYER, ScalesStatsOverlay::render);
    }

    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (VanillaGuiLayers.FOOD_LEVEL.equals(event.getName())) {
            hideVanillaFoodIfNeeded(event);
            return;
        }
        if (isShiftedVanillaLayer(event.getName())) {
            shiftVanillaLayerPre(event);
        }
    }

    public static void onRenderGuiLayerPost(RenderGuiLayerEvent.Post event) {
        if (isShiftedVanillaLayer(event.getName()) && vanillaHudOffset > 0) {
            event.getGuiGraphics().pose().popPose();
        }
    }

    private static void hideVanillaFoodIfNeeded(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || isSpectator(player) || isCreative(player)) {
            return;
        }
        if (OversaturationLogic.hasOversaturationHud(player)) {
            event.setCanceled(true);
        }
    }

    private static void shiftVanillaLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || isSpectator(player) || isCreative(player)) {
            vanillaHudOffset = 0;
            return;
        }
        vanillaHudOffset = computeVanillaHudOffset(player);
        if (vanillaHudOffset > 0) {
            event.getGuiGraphics().pose().pushPose();
            event.getGuiGraphics().pose().translate(0f, -vanillaHudOffset, 0f);
        }
    }

    private static boolean isShiftedVanillaLayer(ResourceLocation name) {
        return VanillaGuiLayers.ARMOR_LEVEL.equals(name) || VanillaGuiLayers.AIR_LEVEL.equals(name);
    }

    public static int computeVanillaHudOffset(LocalPlayer player) {
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        float maxFat = OversaturationLogic.getMaxFatArmorHp(stack);
        if (maxFat <= 0f) {
            return 0;
        }
        int healthHearts = HudHeartLayout.getHealthHeartCount(player);
        int maxFatHeartCount = Mth.ceil(maxFat / 2.0f);
        if (maxFatHeartCount <= healthHearts) {
            return 0;
        }
        int modTopRow = HudHeartLayout.getModTopRow(player, maxFatHeartCount);
        int vanillaTopRow = HudHeartLayout.getVanillaRowsAboveHealth(player);
        return Math.max(0, modTopRow - vanillaTopRow) * ROW_HEIGHT;
    }

    private static boolean isSpectator(LocalPlayer player) {
        return player.isSpectator();
    }

    private static boolean isCreative(LocalPlayer player) {
        return player.getAbilities().instabuild;
    }

    private static boolean hasHungerEffect(LocalPlayer player) {
        return player.hasEffect(MobEffects.HUNGER);
    }

    private static void blitIcon(GuiGraphics graphics, ResourceLocation texture, int x, int y) {
        graphics.blit(texture, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private static void blitHeartSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y) {
        graphics.blitSprite(sprite, x, y, ICON_SIZE, ICON_SIZE);
    }

    private static void renderCounter(GuiGraphics graphics, net.minecraft.client.DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || isSpectator(player)) {
            return;
        }

        OversaturationData data = OversaturationAttachments.get(player);
        if (!OversaturationLogic.hasOversaturationHud(player)) {
            return;
        }

        float points = data.getPoints();
        int display = Mth.clamp(OversaturationLogic.getLevel(points), 0, OversaturationLogic.getMaxStacks());
        if (display <= 0) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int x = width / 2 + 91 + 6;
        int y = height - 39 - 1;
        String text = String.valueOf(display);

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0f);
        pose.scale(COUNTER_SCALE, COUNTER_SCALE, 1f);
        graphics.drawString(minecraft.font, text, 0, 0, 0xFFFF55, true);
        pose.popPose();
    }

    private static void renderFoodLevelFill(GuiGraphics graphics, net.minecraft.client.DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || isSpectator(player) || isCreative(player)) {
            return;
        }
        if (!OversaturationLogic.hasOversaturationHud(player)) {
            return;
        }

        boolean hungerEffect = hasHungerEffect(player);
        ResourceLocation fullIcon = hungerEffect ? FOOD_FULL_HUNGER : BURGER_FULL;
        ResourceLocation halfIcon = hungerEffect ? FOOD_HALF_HUNGER : BURGER_HALF;

        float points = OversaturationAttachments.get(player).getPoints();
        int halfUnits = OversaturationLogic.getFoodHudHalfUnits(points);
        int fullBurgers = halfUnits / 2;
        boolean halfBurger = (halfUnits % 2) != 0;

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int foodRight = width / 2 + 91;
        int foodTop = height - 39;

        for (int iconIndex = 0; iconIndex < FOOD_ICONS; iconIndex++) {
            drawFoodSlot(graphics, foodRight, foodTop, iconIndex, BURGER_FULL_BG, null);
        }

        int iconIndex = 0;
        for (int i = 0; i < fullBurgers && iconIndex < FOOD_ICONS; i++, iconIndex++) {
            drawFoodSlot(graphics, foodRight, foodTop, iconIndex, null, fullIcon);
        }
        if (halfBurger && iconIndex < FOOD_ICONS) {
            drawFoodSlot(graphics, foodRight, foodTop, iconIndex, BURGER_FULL_BG, halfIcon);
        }
    }

    private static void drawFoodSlot(GuiGraphics graphics, int foodRight, int foodTop, int iconIndex,
            ResourceLocation background, ResourceLocation icon) {
        int x = foodRight - iconIndex * ICON_SPACING - ICON_SIZE;
        if (background != null) {
            blitIcon(graphics, background, x, foodTop);
        }
        if (icon != null) {
            blitIcon(graphics, icon, x, foodTop);
        }
    }

    private static void renderFatHearts(GuiGraphics graphics, net.minecraft.client.DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || isSpectator(player) || isCreative(player)) {
            return;
        }

        OversaturationData data = OversaturationAttachments.get(player);
        int stack = OversaturationLogic.getLevel(data.getPoints());
        float maxFat = OversaturationLogic.getMaxFatArmorHp(stack);
        if (maxFat <= 0f) {
            return;
        }

        float fatArmor = data.getFatArmor();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int left = width / 2 - 91;
        int top = height - 39;

        int maxFatHeartCount = Mth.ceil(maxFat / 2.0f);

        for (int i = 0; i < maxFatHeartCount; i++) {
            HudHeartLayout.HeartSlot slot = HudHeartLayout.getFatHeartSlot(i);
            int x = left + slot.column() * ICON_SPACING;
            int y = top - slot.row() * ROW_HEIGHT;

            float remaining = fatArmor - i * 2.0f;
            boolean hasHeart = remaining > 0f;
            boolean half = hasHeart && remaining < 2.0f;

            if (slot.row() >= 1) {
                ResourceLocation bg = half ? FAT_HEART_HALF_BG : FAT_HEART_FULL_BG;
                blitIcon(graphics, bg, x, y);
            }

            if (hasHeart) {
                ResourceLocation texture = half ? FAT_HEART_HALF : FAT_HEART_FULL;
                blitIcon(graphics, texture, x, y);
            }
        }
    }

    private static void renderRelocatedAbsorption(GuiGraphics graphics, net.minecraft.client.DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || isSpectator(player) || isCreative(player)) {
            return;
        }

        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        float maxFat = OversaturationLogic.getMaxFatArmorHp(stack);
        int maxFatHeartCount = Mth.ceil(maxFat / 2.0f);
        if (!HudHeartLayout.shouldRelocateAbsorption(player, maxFatHeartCount)) {
            return;
        }

        int absorptionHearts = HudHeartLayout.getAbsorptionHeartCount(player);
        float absorption = player.getAbsorptionAmount();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int left = width / 2 - 91;
        int top = height - 39;

        for (int i = 0; i < absorptionHearts; i++) {
            HudHeartLayout.HeartSlot slot = HudHeartLayout.getAbsorptionHeartSlot(i, maxFatHeartCount);
            int x = left + slot.column() * ICON_SPACING;
            int y = top - slot.row() * ROW_HEIGHT;

            float remaining = absorption - i * 2.0f;
            if (remaining <= 0f) {
                continue;
            }
            boolean half = remaining < 2.0f;
            ResourceLocation sprite = half ? ABSORPTION_HEART_HALF : ABSORPTION_HEART_FULL;
            blitHeartSprite(graphics, sprite, x, y);
        }
    }
}
