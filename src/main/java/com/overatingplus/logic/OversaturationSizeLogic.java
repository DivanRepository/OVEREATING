package com.overatingplus.logic;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityDimensions;

public final class OversaturationSizeLogic {
    private static final float VANILLA_WIDTH = 0.6F;
    private static final float VANILLA_HEIGHT = 1.8F;
    private static final float VANILLA_EYE_RATIO = 0.9F;

    private OversaturationSizeLogic() {
    }

    private static boolean isClient() {
        return net.minecraft.client.Minecraft.getInstance().level != null;
    }

    public static float getGrowthProgress(int stack) {
        int start = isClient() ? OversaturationLogic.CLIENT_SIZE_START_LEVEL : ModConfig.SIZE_START_LEVEL.get();
        int end = isClient() ? OversaturationLogic.CLIENT_SIZE_MAX_LEVEL : ModConfig.SIZE_MAX_LEVEL.get();
        if (stack < start || end <= start) {
            return 0f;
        }
        return Mth.clamp((stack - start) / (float) (end - start), 0f, 1f);
    }

    public static int getStack(Player player) {
        return OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
    }

    public static boolean shouldApplySize(Player player) {
        if (getGrowthProgress(getStack(player)) <= 0f) {
            return false;
        }
        if (player.isSleeping()) {
            return false;
        }
        // Note: isPassenger() check removed - size should apply even when riding entities
        return true;
    }

    public static float getTargetWidth(int stack) {
        double targetWidth = isClient() ? OversaturationLogic.CLIENT_SIZE_TARGET_WIDTH : ModConfig.SIZE_TARGET_WIDTH.get();
        return applyAxisGrowth(stack, VANILLA_WIDTH, (float) targetWidth, horizontalHitboxCoeff());
    }

    public static float getTargetStandingHeight(int stack) {
        double targetHeight = isClient() ? OversaturationLogic.CLIENT_SIZE_TARGET_HEIGHT : ModConfig.SIZE_TARGET_HEIGHT.get();
        double axisY = isClient() ? OversaturationLogic.CLIENT_SIZE_AXIS_Y : ModConfig.SIZE_AXIS_Y.get();
        return applyAxisGrowth(stack, VANILLA_HEIGHT, (float) targetHeight, (float) axisY);
    }

    public static float getTorsoScale(int stack) {
        float t = getGrowthProgress(stack);
        double maxScale = isClient() ? OversaturationLogic.CLIENT_SIZE_TORSO_MAX_SCALE : ModConfig.SIZE_TORSO_MAX_SCALE.get();
        return Mth.lerp(t, 1.0F, (float) maxScale);
    }

    public static float getTorsoAxisScale(int stack, float axisCoeff) {
        float delta = getTorsoScale(stack) - 1.0F;
        return 1.0F + delta * axisCoeff;
    }

    private static float applyAxisGrowth(int stack, float vanilla, float target, float axisCoeff) {
        float t = getGrowthProgress(stack);
        return vanilla + t * (target - vanilla) * axisCoeff;
    }

    private static float horizontalHitboxCoeff() {
        if (isClient()) {
            return (float) ((OversaturationLogic.CLIENT_SIZE_AXIS_X + OversaturationLogic.CLIENT_SIZE_AXIS_Z) * 0.5D);
        }
        return (float) ((ModConfig.SIZE_AXIS_X.get() + ModConfig.SIZE_AXIS_Z.get()) * 0.5D);
    }

    public static float getCameraPullback(Player player) {
        if (!shouldApplySize(player)) {
            return 0f;
        }
        float t = getGrowthProgress(getStack(player));
        double pullback = isClient() ? OversaturationLogic.CLIENT_SIZE_CAMERA_PULLBACK : ModConfig.SIZE_CAMERA_PULLBACK.get();
        return t * (float) pullback;
    }

    public static EntityDimensions scaleDimensions(Player player, Pose pose, EntityDimensions base) {
        int stack = getStack(player);
        float width = getTargetWidth(stack);
        float standingHeight = getTargetStandingHeight(stack);

        float height = standingHeight;
        if (pose == Pose.CROUCHING) {
            float crouchRatio = 1.5F / VANILLA_HEIGHT;
            height = standingHeight * crouchRatio;
        } else if (pose == Pose.SWIMMING || pose == Pose.FALL_FLYING || pose == Pose.SPIN_ATTACK) {
            float swimRatio = 0.6F / VANILLA_HEIGHT;
            height = standingHeight * swimRatio;
            width = Math.max(width, standingHeight * swimRatio);
        } else if (pose != Pose.STANDING) {
            float ratio = base.height() / VANILLA_HEIGHT;
            height = standingHeight * ratio;
            width = Math.max(width * ratio, base.width() * (width / VANILLA_WIDTH));
        }

        if (base.width() <= 0f || base.height() <= 0f) {
            return EntityDimensions.scalable(width, height).withEyeHeight(height * VANILLA_EYE_RATIO);
        }
        float widthRatio = width / base.width();
        float heightRatio = height / base.height();
        return base.scale(widthRatio, heightRatio);
    }

    public static void refreshPlayerSize(Player player) {
        player.refreshDimensions();
        if (!player.level().isClientSide()) {
            resolveStuckInBlocks(player);
        }
    }

    public static void resolveStuckInBlocks(Player player) {
        Level level = player.level();
        AABB box = player.getBoundingBox();
        if (level.noCollision(player, box)) {
            return;
        }

        Vec3 pos = player.position();
        for (int attempt = 0; attempt < 8; attempt++) {
            double lift = 0.125D * (attempt + 1);
            AABB lifted = box.move(0.0D, lift, 0.0D);
            if (level.noCollision(player, lifted)) {
                player.setPos(pos.x, pos.y + lift, pos.z);
                player.setDeltaMovement(player.getDeltaMovement().x, Math.max(player.getDeltaMovement().y, 0.0D), player.getDeltaMovement().z);
                return;
            }
        }

        for (int attempt = 0; attempt < 6; attempt++) {
            double push = 0.1D * (attempt + 1);
            for (Vec3 offset : new Vec3[]{
                    new Vec3(push, 0.0D, 0.0D),
                    new Vec3(-push, 0.0D, 0.0D),
                    new Vec3(0.0D, 0.0D, push),
                    new Vec3(0.0D, 0.0D, -push)
            }) {
                if (level.noCollision(player, box.move(offset))) {
                    player.setPos(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);
                    return;
                }
            }
        }
    }
}
