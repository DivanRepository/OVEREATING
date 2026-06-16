package com.overatingplus.network;

import com.overatingplus.OveratingPlusMod;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ConfigSyncPayload(
        int maxOversaturationStacks,
        double drainSpeedMultiplier,
        double saturationModeThreshold,
        boolean enableFoodVariety,
        int foodVarietyHistorySize,
        int maxFatArmorHp,
        int fatArmorRegenInterval,
        int slowdownStartLevel,
        double slowdownBasePercent,
        double slowdownPerLevelPercent,
        int strengthStartLevel,
        double strengthBasePercent,
        double strengthPerLevelPercent,
        int knockbackResistanceStartLevel,
        double knockbackResistanceBase,
        double knockbackResistancePerLevel,
        int meleeRepulseStartLevel,
        double meleeRepulseBase,
        double meleeRepulsePerLevel,
        int slimeBounceStartLevel,
        double slimeBounceFactor,
        double slimeBounceChainDamping,
        double slimeBounceMinFallSpeed,
        double slimeBounceStopSpeed,
        int shockwaveStartLevel,
        double shockwaveMinFallBlocks,
        double shockwaveRadius,
        double shockwaveKnockback,
        double slimeBounceCost,
        double shockwaveCost,
        int sizeStartLevel,
        int sizeMaxLevel,
        double sizeTargetWidth,
        double sizeTargetHeight,
        double sizeTorsoMaxScale,
        double sizeAxisX,
        double sizeAxisY,
        double sizeAxisZ,
        double sizeCameraPullback,
        int scalesRedstoneMinStack,
        int scalesRedstoneMaxPower,
        int attackDistanceStartLevel,
        double attackDistanceMaxBonus,
        int blockReachStartLevel,
        double blockReachMaxBonus
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ConfigSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(OveratingPlusMod.MOD_ID, "config_sync"));

    public static final StreamCodec<FriendlyByteBuf, ConfigSyncPayload> STREAM_CODEC = StreamCodec.of(
            ConfigSyncPayload::encode,
            ConfigSyncPayload::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConfigSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(payload::applyToClient);
    }

    private static void encode(FriendlyByteBuf buf, ConfigSyncPayload payload) {
        buf.writeVarInt(payload.maxOversaturationStacks);
        buf.writeDouble(payload.drainSpeedMultiplier);
        buf.writeDouble(payload.saturationModeThreshold);
        buf.writeBoolean(payload.enableFoodVariety);
        buf.writeVarInt(payload.foodVarietyHistorySize);
        buf.writeVarInt(payload.maxFatArmorHp);
        buf.writeVarInt(payload.fatArmorRegenInterval);
        buf.writeVarInt(payload.slowdownStartLevel);
        buf.writeDouble(payload.slowdownBasePercent);
        buf.writeDouble(payload.slowdownPerLevelPercent);
        buf.writeVarInt(payload.strengthStartLevel);
        buf.writeDouble(payload.strengthBasePercent);
        buf.writeDouble(payload.strengthPerLevelPercent);
        buf.writeVarInt(payload.knockbackResistanceStartLevel);
        buf.writeDouble(payload.knockbackResistanceBase);
        buf.writeDouble(payload.knockbackResistancePerLevel);
        buf.writeVarInt(payload.meleeRepulseStartLevel);
        buf.writeDouble(payload.meleeRepulseBase);
        buf.writeDouble(payload.meleeRepulsePerLevel);
        buf.writeVarInt(payload.slimeBounceStartLevel);
        buf.writeDouble(payload.slimeBounceFactor);
        buf.writeDouble(payload.slimeBounceChainDamping);
        buf.writeDouble(payload.slimeBounceMinFallSpeed);
        buf.writeDouble(payload.slimeBounceStopSpeed);
        buf.writeVarInt(payload.shockwaveStartLevel);
        buf.writeDouble(payload.shockwaveMinFallBlocks);
        buf.writeDouble(payload.shockwaveRadius);
        buf.writeDouble(payload.shockwaveKnockback);
        buf.writeDouble(payload.slimeBounceCost);
        buf.writeDouble(payload.shockwaveCost);
        buf.writeVarInt(payload.sizeStartLevel);
        buf.writeVarInt(payload.sizeMaxLevel);
        buf.writeDouble(payload.sizeTargetWidth);
        buf.writeDouble(payload.sizeTargetHeight);
        buf.writeDouble(payload.sizeTorsoMaxScale);
        buf.writeDouble(payload.sizeAxisX);
        buf.writeDouble(payload.sizeAxisY);
        buf.writeDouble(payload.sizeAxisZ);
        buf.writeDouble(payload.sizeCameraPullback);
        buf.writeVarInt(payload.scalesRedstoneMinStack);
        buf.writeVarInt(payload.scalesRedstoneMaxPower);
        buf.writeVarInt(payload.attackDistanceStartLevel);
        buf.writeDouble(payload.attackDistanceMaxBonus);
        buf.writeVarInt(payload.blockReachStartLevel);
        buf.writeDouble(payload.blockReachMaxBonus);
    }

    private static ConfigSyncPayload decode(FriendlyByteBuf buf) {
        return new ConfigSyncPayload(
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readBoolean(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readDouble(),
                buf.readVarInt(),
                buf.readDouble()
        );
    }

    public static ConfigSyncPayload fromServerConfig() {
        return new ConfigSyncPayload(
                com.overatingplus.ModConfig.MAX_OVERSATURATION_STACKS.get(),
                com.overatingplus.ModConfig.DRAIN_SPEED_MULTIPLIER.get(),
                com.overatingplus.ModConfig.SATURATION_MODE_THRESHOLD.get(),
                com.overatingplus.ModConfig.ENABLE_FOOD_VARIETY.get(),
                com.overatingplus.ModConfig.FOOD_VARIETY_HISTORY_SIZE.get(),
                com.overatingplus.ModConfig.MAX_FAT_ARMOR_HP.get(),
                com.overatingplus.ModConfig.FAT_ARMOR_REGEN_INTERVAL.get(),
                com.overatingplus.ModConfig.SLOWDOWN_START_LEVEL.get(),
                com.overatingplus.ModConfig.SLOWDOWN_BASE_PERCENT.get(),
                com.overatingplus.ModConfig.SLOWDOWN_PER_LEVEL_PERCENT.get(),
                com.overatingplus.ModConfig.STRENGTH_START_LEVEL.get(),
                com.overatingplus.ModConfig.STRENGTH_BASE_PERCENT.get(),
                com.overatingplus.ModConfig.STRENGTH_PER_LEVEL_PERCENT.get(),
                com.overatingplus.ModConfig.KNOCKBACK_RESISTANCE_START_LEVEL.get(),
                com.overatingplus.ModConfig.KNOCKBACK_RESISTANCE_BASE.get(),
                com.overatingplus.ModConfig.KNOCKBACK_RESISTANCE_PER_LEVEL.get(),
                com.overatingplus.ModConfig.MELEE_REPULSE_START_LEVEL.get(),
                com.overatingplus.ModConfig.MELEE_REPULSE_BASE.get(),
                com.overatingplus.ModConfig.MELEE_REPULSE_PER_LEVEL.get(),
                com.overatingplus.ModConfig.SLIME_BOUNCE_START_LEVEL.get(),
                com.overatingplus.ModConfig.SLIME_BOUNCE_FACTOR.get(),
                com.overatingplus.ModConfig.SLIME_BOUNCE_CHAIN_DAMPING.get(),
                com.overatingplus.ModConfig.SLIME_BOUNCE_MIN_FALL_SPEED.get(),
                com.overatingplus.ModConfig.SLIME_BOUNCE_STOP_SPEED.get(),
                com.overatingplus.ModConfig.SHOCKWAVE_START_LEVEL.get(),
                com.overatingplus.ModConfig.SHOCKWAVE_MIN_FALL_BLOCKS.get(),
                com.overatingplus.ModConfig.SHOCKWAVE_RADIUS.get(),
                com.overatingplus.ModConfig.SHOCKWAVE_KNOCKBACK.get(),
                com.overatingplus.ModConfig.SLIME_BOUNCE_COST.get(),
                com.overatingplus.ModConfig.SHOCKWAVE_COST.get(),
                com.overatingplus.ModConfig.SIZE_START_LEVEL.get(),
                com.overatingplus.ModConfig.SIZE_MAX_LEVEL.get(),
                com.overatingplus.ModConfig.SIZE_TARGET_WIDTH.get(),
                com.overatingplus.ModConfig.SIZE_TARGET_HEIGHT.get(),
                com.overatingplus.ModConfig.SIZE_TORSO_MAX_SCALE.get(),
                com.overatingplus.ModConfig.SIZE_AXIS_X.get(),
                com.overatingplus.ModConfig.SIZE_AXIS_Y.get(),
                com.overatingplus.ModConfig.SIZE_AXIS_Z.get(),
                com.overatingplus.ModConfig.SIZE_CAMERA_PULLBACK.get(),
                com.overatingplus.ModConfig.SCALES_REDSTONE_MIN_STACK.get(),
                com.overatingplus.ModConfig.SCALES_REDSTONE_MAX_POWER.get(),
                com.overatingplus.ModConfig.ATTACK_DISTANCE_START_LEVEL.get(),
                com.overatingplus.ModConfig.ATTACK_DISTANCE_MAX_BONUS.get(),
                com.overatingplus.ModConfig.BLOCK_REACH_START_LEVEL.get(),
                com.overatingplus.ModConfig.BLOCK_REACH_MAX_BONUS.get()
        );
    }

    public void applyToClient() {
        OversaturationLogic.CLIENT_MAX_OVERSATURATION_STACKS = this.maxOversaturationStacks;
        OversaturationLogic.CLIENT_DRAIN_SPEED_MULTIPLIER = this.drainSpeedMultiplier;
        OversaturationLogic.CLIENT_SATURATION_MODE_THRESHOLD = this.saturationModeThreshold;
        OversaturationLogic.CLIENT_ENABLE_FOOD_VARIETY = this.enableFoodVariety;
        OversaturationLogic.CLIENT_FOOD_VARIETY_HISTORY_SIZE = this.foodVarietyHistorySize;
        OversaturationLogic.CLIENT_MAX_FAT_ARMOR_HP = this.maxFatArmorHp;
        OversaturationLogic.CLIENT_FAT_ARMOR_REGEN_INTERVAL = this.fatArmorRegenInterval;
        OversaturationLogic.CLIENT_SLOWDOWN_START_LEVEL = this.slowdownStartLevel;
        OversaturationLogic.CLIENT_SLOWDOWN_BASE_PERCENT = this.slowdownBasePercent;
        OversaturationLogic.CLIENT_SLOWDOWN_PER_LEVEL_PERCENT = this.slowdownPerLevelPercent;
        OversaturationLogic.CLIENT_STRENGTH_START_LEVEL = this.strengthStartLevel;
        OversaturationLogic.CLIENT_STRENGTH_BASE_PERCENT = this.strengthBasePercent;
        OversaturationLogic.CLIENT_STRENGTH_PER_LEVEL_PERCENT = this.strengthPerLevelPercent;
        OversaturationLogic.CLIENT_KNOCKBACK_RESISTANCE_START_LEVEL = this.knockbackResistanceStartLevel;
        OversaturationLogic.CLIENT_KNOCKBACK_RESISTANCE_BASE = this.knockbackResistanceBase;
        OversaturationLogic.CLIENT_KNOCKBACK_RESISTANCE_PER_LEVEL = this.knockbackResistancePerLevel;
        OversaturationLogic.CLIENT_MELEE_REPULSE_START_LEVEL = this.meleeRepulseStartLevel;
        OversaturationLogic.CLIENT_MELEE_REPULSE_BASE = this.meleeRepulseBase;
        OversaturationLogic.CLIENT_MELEE_REPULSE_PER_LEVEL = this.meleeRepulsePerLevel;
        OversaturationLogic.CLIENT_SLIME_BOUNCE_START_LEVEL = this.slimeBounceStartLevel;
        OversaturationLogic.CLIENT_SLIME_BOUNCE_FACTOR = this.slimeBounceFactor;
        OversaturationLogic.CLIENT_SLIME_BOUNCE_CHAIN_DAMPING = this.slimeBounceChainDamping;
        OversaturationLogic.CLIENT_SLIME_BOUNCE_MIN_FALL_SPEED = this.slimeBounceMinFallSpeed;
        OversaturationLogic.CLIENT_SLIME_BOUNCE_STOP_SPEED = this.slimeBounceStopSpeed;
        OversaturationLogic.CLIENT_SHOCKWAVE_START_LEVEL = this.shockwaveStartLevel;
        OversaturationLogic.CLIENT_SHOCKWAVE_MIN_FALL_BLOCKS = this.shockwaveMinFallBlocks;
        OversaturationLogic.CLIENT_SHOCKWAVE_RADIUS = this.shockwaveRadius;
        OversaturationLogic.CLIENT_SHOCKWAVE_KNOCKBACK = this.shockwaveKnockback;
        OversaturationLogic.CLIENT_SLIME_BOUNCE_COST = this.slimeBounceCost;
        OversaturationLogic.CLIENT_SHOCKWAVE_COST = this.shockwaveCost;
        OversaturationLogic.CLIENT_SIZE_START_LEVEL = this.sizeStartLevel;
        OversaturationLogic.CLIENT_SIZE_MAX_LEVEL = this.sizeMaxLevel;
        OversaturationLogic.CLIENT_SIZE_TARGET_WIDTH = this.sizeTargetWidth;
        OversaturationLogic.CLIENT_SIZE_TARGET_HEIGHT = this.sizeTargetHeight;
        OversaturationLogic.CLIENT_SIZE_TORSO_MAX_SCALE = this.sizeTorsoMaxScale;
        OversaturationLogic.CLIENT_SIZE_AXIS_X = this.sizeAxisX;
        OversaturationLogic.CLIENT_SIZE_AXIS_Y = this.sizeAxisY;
        OversaturationLogic.CLIENT_SIZE_AXIS_Z = this.sizeAxisZ;
        OversaturationLogic.CLIENT_SIZE_CAMERA_PULLBACK = this.sizeCameraPullback;
        OversaturationLogic.CLIENT_SCALES_REDSTONE_MIN_STACK = this.scalesRedstoneMinStack;
        OversaturationLogic.CLIENT_SCALES_REDSTONE_MAX_POWER = this.scalesRedstoneMaxPower;
        OversaturationLogic.CLIENT_ATTACK_DISTANCE_START_LEVEL = this.attackDistanceStartLevel;
        OversaturationLogic.CLIENT_ATTACK_DISTANCE_MAX_BONUS = this.attackDistanceMaxBonus;
        OversaturationLogic.CLIENT_BLOCK_REACH_START_LEVEL = this.blockReachStartLevel;
        OversaturationLogic.CLIENT_BLOCK_REACH_MAX_BONUS = this.blockReachMaxBonus;
    }
}
