package com.overatingplus.logic;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.util.FoodAccumulationSource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class OversaturationLogic {
    public static final int POINTS_PER_STACK = 20;

    public static final ResourceLocation SLOWDOWN_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("overatingplus", "oversaturation_slowdown");
    public static final ResourceLocation STRENGTH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("overatingplus", "oversaturation_strength");
    public static final ResourceLocation KNOCKBACK_RESISTANCE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("overatingplus", "oversaturation_knockback_resistance");
    public static final ResourceLocation ATTACK_RANGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("overatingplus", "oversaturation_attack_range");
    public static final ResourceLocation BLOCK_REACH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("overatingplus", "oversaturation_block_reach");

    // Client-side cached config values (synced from server)
    public static int CLIENT_MAX_OVERSATURATION_STACKS = 40;
    public static double CLIENT_DRAIN_SPEED_MULTIPLIER = 1.15;
    public static double CLIENT_SATURATION_MODE_THRESHOLD = 5.0;
    public static boolean CLIENT_ENABLE_FOOD_VARIETY = true;
    public static int CLIENT_FOOD_VARIETY_HISTORY_SIZE = 20;
    public static int CLIENT_MAX_FAT_ARMOR_HP = 40;
    public static int CLIENT_FAT_ARMOR_REGEN_INTERVAL = 20;
    public static int CLIENT_SLOWDOWN_START_LEVEL = 3;
    public static double CLIENT_SLOWDOWN_BASE_PERCENT = 0.10;
    public static double CLIENT_SLOWDOWN_PER_LEVEL_PERCENT = 0.01;
    public static int CLIENT_STRENGTH_START_LEVEL = 3;
    public static double CLIENT_STRENGTH_BASE_PERCENT = 0.05;
    public static double CLIENT_STRENGTH_PER_LEVEL_PERCENT = 0.01;
    public static int CLIENT_KNOCKBACK_RESISTANCE_START_LEVEL = 3;
    public static double CLIENT_KNOCKBACK_RESISTANCE_BASE = 0.05;
    public static double CLIENT_KNOCKBACK_RESISTANCE_PER_LEVEL = 0.02;
    public static int CLIENT_MELEE_REPULSE_START_LEVEL = 3;
    public static double CLIENT_MELEE_REPULSE_BASE = 0.35;
    public static double CLIENT_MELEE_REPULSE_PER_LEVEL = 0.05;
    public static int CLIENT_SLIME_BOUNCE_START_LEVEL = 12;
    public static double CLIENT_SLIME_BOUNCE_FACTOR = 0.66;
    public static double CLIENT_SLIME_BOUNCE_CHAIN_DAMPING = 0.75;
    public static double CLIENT_SLIME_BOUNCE_MIN_FALL_SPEED = 0.15;
    public static double CLIENT_SLIME_BOUNCE_STOP_SPEED = 0.08;
    public static int CLIENT_SHOCKWAVE_START_LEVEL = 20;
    public static double CLIENT_SHOCKWAVE_MIN_FALL_BLOCKS = 1.5;
    public static double CLIENT_SHOCKWAVE_RADIUS = 3.5;
    public static double CLIENT_SHOCKWAVE_KNOCKBACK = 1.2;
    public static int CLIENT_SIZE_START_LEVEL = 10;
    public static int CLIENT_SIZE_MAX_LEVEL = 40;
    public static double CLIENT_SIZE_TARGET_WIDTH = 2.0;
    public static double CLIENT_SIZE_TARGET_HEIGHT = 2.5;
    public static double CLIENT_SIZE_TORSO_MAX_SCALE = 2.2;
    public static double CLIENT_SIZE_AXIS_X = 0.5;
    public static double CLIENT_SIZE_AXIS_Y = 1.0;
    public static double CLIENT_SIZE_AXIS_Z = 0.5;
    public static double CLIENT_SIZE_CAMERA_PULLBACK = 1.5;
    public static int CLIENT_SCALES_REDSTONE_MIN_STACK = 1;
    public static int CLIENT_SCALES_REDSTONE_MAX_POWER = 15;
    public static double CLIENT_SLIME_BOUNCE_COST = 5.0;
    public static double CLIENT_SHOCKWAVE_COST = 5.0;
    public static int CLIENT_ATTACK_DISTANCE_START_LEVEL = 1;
    public static double CLIENT_ATTACK_DISTANCE_MAX_BONUS = 1.0;
    public static int CLIENT_BLOCK_REACH_START_LEVEL = 1;
    public static double CLIENT_BLOCK_REACH_MAX_BONUS = 1.0;

    private OversaturationLogic() {
    }

    private static boolean isClient() {
        return net.minecraft.client.Minecraft.getInstance().level != null;
    }

    public static int getPointsPerLevel() {
        return POINTS_PER_STACK;
    }

    public static int getMaxStacks() {
        if (isClient()) {
            return CLIENT_MAX_OVERSATURATION_STACKS;
        }
        return ModConfig.MAX_OVERSATURATION_STACKS.get();
    }

    public static float getMaxPoints() {
        return POINTS_PER_STACK * getMaxStacks();
    }

    public static float clampPoints(float points) {
        if (points <= 0f) {
            return 0f;
        }
        return Mth.clamp(points, 0f, getMaxPoints());
    }

    public static boolean hasOversaturationHud(Player player) {
        return OversaturationAttachments.get(player).getPoints() > 0f;
    }

    public static int getLevel(float points) {
        int perLevel = getPointsPerLevel();
        if (perLevel <= 0) {
            return 0;
        }
        return Mth.clamp((int) (points / perLevel), 0, getMaxStacks());
    }

    public static float getPointsInCurrentStack(float points) {
        int perLevel = getPointsPerLevel();
        if (perLevel <= 0 || points <= 0f) {
            return 0f;
        }
        float remainder = points % perLevel;
        return remainder == 0f ? perLevel : remainder;
    }

    /** Half-burger icons for the HUD; last point keeps a half icon until points reach 0. */
    public static int getFoodHudHalfUnits(float points) {
        if (points <= 0f) {
            return 0;
        }
        return Mth.ceil(getPointsInCurrentStack(points));
    }

    public static boolean isOversaturated(OversaturationData data) {
        return data.getPoints() > 0f;
    }

    public static boolean canGainOversaturation(Player player) {
        FoodData food = player.getFoodData();
        double threshold = isClient() ? CLIENT_SATURATION_MODE_THRESHOLD : ModConfig.SATURATION_MODE_THRESHOLD.get();
        return food.getFoodLevel() >= 20
                && food.getSaturationLevel() >= threshold;
    }

    public static boolean isInOversaturationMode(Player player) {
        return canGainOversaturation(player);
    }

    public static float computeFoodGain(FoodProperties food) {
        if (isClient()) {
            if (com.overatingplus.ModConfig.ACCUMULATION_SOURCE.get() == FoodAccumulationSource.SATURATION) {
                return food.saturation();
            }
            return food.nutrition();
        }
        if (ModConfig.ACCUMULATION_SOURCE.get() == FoodAccumulationSource.SATURATION) {
            return food.saturation();
        }
        return food.nutrition();
    }

    public static float applyPointGain(float current, float gain) {
        return clampPoints(current + gain);
    }

    public static float getSlowdownPercent(int stack) {
        if (isClient()) {
            return getScaledBonus(stack, CLIENT_SLOWDOWN_START_LEVEL, CLIENT_SLOWDOWN_BASE_PERCENT, CLIENT_SLOWDOWN_PER_LEVEL_PERCENT);
        }
        return getScaledBonus(
                stack,
                ModConfig.SLOWDOWN_START_LEVEL.get(),
                ModConfig.SLOWDOWN_BASE_PERCENT.get(),
                ModConfig.SLOWDOWN_PER_LEVEL_PERCENT.get()
        );
    }

    public static float getStrengthPercent(int stack) {
        if (isClient()) {
            return getScaledBonus(stack, CLIENT_STRENGTH_START_LEVEL, CLIENT_STRENGTH_BASE_PERCENT, CLIENT_STRENGTH_PER_LEVEL_PERCENT);
        }
        return getScaledBonus(
                stack,
                ModConfig.STRENGTH_START_LEVEL.get(),
                ModConfig.STRENGTH_BASE_PERCENT.get(),
                ModConfig.STRENGTH_PER_LEVEL_PERCENT.get()
        );
    }

    public static float getKnockbackResistanceBonus(int stack) {
        if (isClient()) {
            return Mth.clamp(getScaledBonus(stack, CLIENT_KNOCKBACK_RESISTANCE_START_LEVEL, CLIENT_KNOCKBACK_RESISTANCE_BASE, CLIENT_KNOCKBACK_RESISTANCE_PER_LEVEL), 0f, 1f);
        }
        return Mth.clamp(getScaledBonus(
                stack,
                ModConfig.KNOCKBACK_RESISTANCE_START_LEVEL.get(),
                ModConfig.KNOCKBACK_RESISTANCE_BASE.get(),
                ModConfig.KNOCKBACK_RESISTANCE_PER_LEVEL.get()
        ), 0f, 1f);
    }

    public static double getMeleeRepulseStrength(int stack) {
        if (isClient()) {
            if (stack < CLIENT_MELEE_REPULSE_START_LEVEL) {
                return 0d;
            }
            return CLIENT_MELEE_REPULSE_BASE + (stack - CLIENT_MELEE_REPULSE_START_LEVEL) * CLIENT_MELEE_REPULSE_PER_LEVEL;
        }
        int start = ModConfig.MELEE_REPULSE_START_LEVEL.get();
        if (stack < start) {
            return 0d;
        }
        return ModConfig.MELEE_REPULSE_BASE.get()
                + (stack - start) * ModConfig.MELEE_REPULSE_PER_LEVEL.get();
    }

    private static float getScaledBonus(int stack, int startLevel, double base, double perLevel) {
        if (stack < startLevel) {
            return 0f;
        }
        return (float) (base + (stack - startLevel) * perLevel);
    }

    public static float getMaxFatArmorHp(int stack) {
        if (stack <= 0) {
            return 0f;
        }
        float capHp = isClient() ? (float) CLIENT_MAX_FAT_ARMOR_HP : ModConfig.MAX_FAT_ARMOR_HP.get().floatValue();
        return Mth.clamp((float) stack, 0f, capHp);
    }

    public static void syncFatArmorForStack(Player player, OversaturationData data, int stack) {
        float max = getMaxFatArmorHp(stack);
        if (max <= 0f || !hasFullHealth(player)) {
            return;
        }
        if (data.getFatArmor() < max) {
            data.setFatArmor(max);
        }
    }

    public static boolean hasFullHealth(Player player) {
        return player.getHealth() >= player.getMaxHealth();
    }

    public static boolean hasSlimeBounce(Player player) {
        int threshold = isClient() ? CLIENT_SLIME_BOUNCE_START_LEVEL : ModConfig.SLIME_BOUNCE_START_LEVEL.get();
        return getLevel(OversaturationAttachments.get(player).getPoints()) >= threshold;
    }

    public static boolean canSlimeBounce(Player player) {
        if (!hasSlimeBounce(player) || player.isCrouching()) {
            return false;
        }
        return OversaturationAttachments.get(player).isBounceEnabled();
    }

    public static boolean hasShockwave(Player player) {
        int threshold = isClient() ? CLIENT_SHOCKWAVE_START_LEVEL : ModConfig.SHOCKWAVE_START_LEVEL.get();
        return getLevel(OversaturationAttachments.get(player).getPoints()) >= threshold;
    }

    public static boolean canTriggerShockwave(Player player, OversaturationData data) {
        return hasShockwave(player) && data.isShockwaveEnabled() && !player.isCrouching();
    }

    public static void tryShockwaveOnLand(Player player, OversaturationData data, float fallBlocks) {
        if (!canTriggerShockwave(player, data)) {
            return;
        }
        ShockwaveLogic.trigger(player, fallBlocks);
    }

    public static void clampFatArmorToStack(OversaturationData data, int stack) {
        float max = getMaxFatArmorHp(stack);
        if (data.getFatArmor() > max) {
            data.setFatArmor(max);
        }
    }

    public static void applyMovementPenalty(Player player, int stack) {
        applyStackBonuses(player, stack);
    }

    public static void applyStackBonuses(Player player, int stack) {
        applyAttributeMultiplier(player, Attributes.MOVEMENT_SPEED, SLOWDOWN_MODIFIER_ID, getSlowdownPercent(stack), true);
        applyAttributeMultiplier(player, Attributes.ATTACK_DAMAGE, STRENGTH_MODIFIER_ID, getStrengthPercent(stack), false);
        applyKnockbackResistance(player, stack);
        applyAttackRangeBonus(player, stack);
        applyBlockReachBonus(player, stack);
    }

    private static void applyAttributeMultiplier(
            Player player,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            ResourceLocation modifierId,
            float percent,
            boolean negative
    ) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(modifierId);
        if (percent > 0f) {
            instance.addPermanentModifier(new AttributeModifier(
                    modifierId,
                    negative ? -percent : percent,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }

    private static void applyKnockbackResistance(Player player, int stack) {
        AttributeInstance instance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (instance == null) {
            return;
        }
        instance.removeModifier(KNOCKBACK_RESISTANCE_MODIFIER_ID);
        float bonus = getKnockbackResistanceBonus(stack);
        if (bonus > 0f) {
            instance.addPermanentModifier(new AttributeModifier(
                    KNOCKBACK_RESISTANCE_MODIFIER_ID,
                    bonus,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    private static void applyAttackRangeBonus(Player player, int stack) {
        // Try to get the attribute holder from the registry
        var attributeHolder = getAttributeHolder("minecraft", "player.entity_interaction_range");
        if (attributeHolder == null) {
            return;
        }
        AttributeInstance instance = player.getAttribute(attributeHolder);
        if (instance == null) {
            return;
        }
        instance.removeModifier(ATTACK_RANGE_MODIFIER_ID);
        double bonus = getAttackDistanceBonus(stack);
        if (bonus > 0d) {
            instance.addPermanentModifier(new AttributeModifier(
                    ATTACK_RANGE_MODIFIER_ID,
                    bonus,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    private static void applyBlockReachBonus(Player player, int stack) {
        // Try to get the attribute holder from the registry
        var attributeHolder = getAttributeHolder("minecraft", "player.block_interaction_range");
        if (attributeHolder == null) {
            return;
        }
        AttributeInstance instance = player.getAttribute(attributeHolder);
        if (instance == null) {
            return;
        }
        instance.removeModifier(BLOCK_REACH_MODIFIER_ID);
        double bonus = getBlockReachBonus(stack);
        if (bonus > 0d) {
            instance.addPermanentModifier(new AttributeModifier(
                    BLOCK_REACH_MODIFIER_ID,
                    bonus,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    @SuppressWarnings("unchecked")
    private static net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> getAttributeHolder(String namespace, String path) {
        var registry = net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE;
        var key = net.minecraft.resources.ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(namespace, path));
        return registry.getHolder(key).orElse(null);
    }

    public static void updateFatArmor(Player player, OversaturationData data, int stack, int previousStack) {
        if (stack <= 0) {
            data.setFatArmor(0f);
            return;
        }

        float max = getMaxFatArmorHp(stack);
        float current = data.getFatArmor();

        if (previousStack <= 0) {
            if (hasFullHealth(player)) {
                data.setFatArmor(max);
            } else {
                data.setFatArmor(0f);
            }
            return;
        }

        if (stack > previousStack) {
            float previousMax = getMaxFatArmorHp(previousStack);
            float increase = max - previousMax;
            if (hasFullHealth(player) && increase > 0f) {
                data.setFatArmor(Mth.clamp(current + increase, 0f, max));
            } else {
                data.setFatArmor(Math.min(current, max));
            }
            return;
        }

        if (stack < previousStack) {
            data.setFatArmor(Math.min(current, max));
        } else if (current > max) {
            data.setFatArmor(max);
        }
    }

    public static void regenFatArmor(Player player, OversaturationData data, int stack) {
        if (!hasFullHealth(player)) {
            return;
        }
        int interval = isClient() ? CLIENT_FAT_ARMOR_REGEN_INTERVAL : ModConfig.FAT_ARMOR_REGEN_INTERVAL.get();
        if (player.tickCount % interval != 0) {
            return;
        }
        float max = getMaxFatArmorHp(stack);
        if (max <= 0f || data.getFatArmor() >= max || data.getPoints() <= 1f) {
            return;
        }
        data.setFatArmor(Math.min(data.getFatArmor() + 1f, max));
        data.setPoints(clampPoints(data.getPoints() - 1f));
    }

    /**
     * Slime-block-like bounce on all blocks from stack 12+. No slowdown (we do not touch block friction).
     */
    public static void handleSlimeMovement(Player player, OversaturationData data) {
        if (player.isSpectator() || player.getAbilities().instabuild) {
            return;
        }
        if (!canSlimeBounce(player)) {
            data.setWasOnGround(player.onGround());
            data.resetBounceChain();
            return;
        }

        double baseFactor = isClient() ? CLIENT_SLIME_BOUNCE_FACTOR : ModConfig.SLIME_BOUNCE_FACTOR.get();
        double chainDamping = isClient() ? CLIENT_SLIME_BOUNCE_CHAIN_DAMPING : ModConfig.SLIME_BOUNCE_CHAIN_DAMPING.get();
        double minFallSpeed = isClient() ? CLIENT_SLIME_BOUNCE_MIN_FALL_SPEED : ModConfig.SLIME_BOUNCE_MIN_FALL_SPEED.get();
        double stopSpeed = isClient() ? CLIENT_SLIME_BOUNCE_STOP_SPEED : ModConfig.SLIME_BOUNCE_STOP_SPEED.get();

        Vec3 motion = player.getDeltaMovement();
        boolean onGround = player.onGround();

        if (!onGround) {
            if (motion.y < -minFallSpeed * 0.5D) {
                data.setFallMotionY(Math.min(data.getFallMotionY(), motion.y));
            }
            if (motion.y > 0.2D) {
                data.resetBounceChain();
            }
        } else {
            // Only bounce if we just landed (wasOnGround=false), have enough fall speed, and cooldown is not active
            if (!data.wasOnGround() && data.getFallMotionY() < -minFallSpeed && !data.isBounceCooldown()) {
                double effectiveFactor = baseFactor * Math.pow(chainDamping, data.getBounceChain());
                double bounceY = -data.getFallMotionY() * effectiveFactor;

                if (bounceY < stopSpeed) {
                    player.setDeltaMovement(motion.x, 0.0D, motion.z);
                    data.resetBounceChain();
                } else {
                    player.setDeltaMovement(motion.x, bounceY, motion.z);
                    data.setBounceChain(data.getBounceChain() + 1);
                    player.hurtMarked = true;
                    data.setFallMotionY(0);
                    data.setBounceCooldown(true); // Prevent multiple deductions per landing
                    deductBounceCost(player, data);
                }
                player.resetFallDistance();
            } else {
                if (Math.abs(motion.y) < stopSpeed) {
                    player.setDeltaMovement(motion.x, 0.0D, motion.z);
                }
                data.resetBounceChain();
            }
        }

        // Wall bounce - also check cooldown
        if (player.horizontalCollision && !data.isBounceCooldown()) {
            double effectiveFactor = baseFactor * Math.pow(chainDamping, data.getBounceChain());
            double newX = motion.x;
            double newZ = motion.z;
            double movedX = player.getX() - player.xo;
            double movedZ = player.getZ() - player.zo;

            if (Math.abs(motion.x) > minFallSpeed && Math.abs(movedX) < Math.abs(motion.x) * 0.2D) {
                newX = -motion.x * effectiveFactor;
                if (Math.abs(newX) < stopSpeed) {
                    newX = 0.0D;
                }
            }
            if (Math.abs(motion.z) > minFallSpeed && Math.abs(movedZ) < Math.abs(motion.z) * 0.2D) {
                newZ = -motion.z * effectiveFactor;
                if (Math.abs(newZ) < stopSpeed) {
                    newZ = 0.0D;
                }
            }
            if (newX != motion.x || newZ != motion.z) {
                player.setDeltaMovement(newX, player.getDeltaMovement().y, newZ);
                player.hurtMarked = true;
                data.setBounceChain(data.getBounceChain() + 1);
                data.setBounceCooldown(true);
                deductBounceCost(player, data);
            }
        }

        data.setWasOnGround(onGround);
    }

    public static void syncPlayerState(Player player) {
        OversaturationData data = OversaturationAttachments.get(player);
        int stack = getLevel(data.getPoints());
        applyStackBonuses(player, stack);
        OversaturationAttachments.set(player, data);
        updatePlayerSize(player, data, stack);
    }

    public static void resetAfterDeath(Player player) {
        OversaturationAttachments.set(player, new OversaturationData());
        applyStackBonuses(player, 0);
        OversaturationSizeLogic.refreshPlayerSize(player);
        player.syncData(OversaturationAttachments.OVERSATURATION.get());
    }

    public static void updatePlayerSize(Player player, OversaturationData data, int stack) {
        boolean active = OversaturationSizeLogic.shouldApplySize(player);
        if (data.getLastAppliedSizeStack() == stack && data.isSizeEffectActive() == active) {
            return;
        }
        data.setLastAppliedSizeStack(stack);
        data.setSizeEffectActive(active);
        OversaturationSizeLogic.refreshPlayerSize(player);
    }

    public static void drainFromExhaustion(Player player, OversaturationData data, FoodData food) {
        if (data.getPoints() <= 0f) {
            data.setLastExhaustionSample(food.getExhaustionLevel());
            return;
        }
        if (player.level().getDifficulty() == Difficulty.PEACEFUL) {
            data.setLastExhaustionSample(food.getExhaustionLevel());
            return;
        }

        float current = food.getExhaustionLevel();
        float previous = data.getLastExhaustionSample();
        data.setLastExhaustionSample(current);

        if (current <= previous) {
            return;
        }

        float delta = current - previous;
        double multiplier = isClient() ? CLIENT_DRAIN_SPEED_MULTIPLIER : ModConfig.DRAIN_SPEED_MULTIPLIER.get();
        float drain = (delta / 4.0f) * (float) multiplier;
        if (drain > 0f) {
            data.setPoints(clampPoints(data.getPoints() - drain));
        }
    }

    public static void drainFromHungerEffect(Player player, OversaturationData data) {
        if (data.getPoints() <= 0f) {
            return;
        }
        var effect = player.getEffect(MobEffects.HUNGER);
        if (effect == null) {
            return;
        }
        double multiplier = isClient() ? CLIENT_DRAIN_SPEED_MULTIPLIER : ModConfig.DRAIN_SPEED_MULTIPLIER.get();
        float drain = (effect.getAmplifier() + 1) * 0.05f * (float) multiplier;
        data.setPoints(clampPoints(data.getPoints() - drain));
    }

    public static boolean shouldDrainOversaturation(Level level) {
        return level.getDifficulty() != Difficulty.PEACEFUL;
    }

    /**
     * Deducts oversaturation points for using slime bounce ability.
     */
    public static void deductBounceCost(Player player, OversaturationData data) {
        double cost = isClient() ? CLIENT_SLIME_BOUNCE_COST : ModConfig.SLIME_BOUNCE_COST.get();
        if (cost > 0f) {
            data.setPoints(clampPoints(data.getPoints() - (float) cost));
        }
    }

    /**
     * Deducts oversaturation points for using shockwave ability.
     */
    public static void deductShockwaveCost(Player player, OversaturationData data) {
        double cost = isClient() ? CLIENT_SHOCKWAVE_COST : ModConfig.SHOCKWAVE_COST.get();
        if (cost > 0f) {
            data.setPoints(clampPoints(data.getPoints() - (float) cost));
        }
    }

    /**
     * Calculates the bonus attack distance based on oversaturation stacks.
     * Returns 0 at start stack, max bonus at max stacks (linear interpolation).
     */
    public static double getAttackDistanceBonus(int stack) {
        int startLevel = isClient() ? CLIENT_ATTACK_DISTANCE_START_LEVEL : ModConfig.ATTACK_DISTANCE_START_LEVEL.get();
        double maxBonus = isClient() ? CLIENT_ATTACK_DISTANCE_MAX_BONUS : ModConfig.ATTACK_DISTANCE_MAX_BONUS.get();
        
        if (stack <= startLevel || maxBonus <= 0d) {
            return 0d;
        }
        
        int maxStacks = getMaxStacks();
        if (maxStacks <= startLevel) {
            return maxBonus;
        }
        
        double progress = (double) (stack - startLevel) / (double) (maxStacks - startLevel);
        return Mth.clamp(progress, 0d, 1d) * maxBonus;
    }

    /**
     * Calculates the bonus block reach distance based on oversaturation stacks.
     * Returns 0 at start stack, max bonus at max stacks (linear interpolation).
     */
    public static double getBlockReachBonus(int stack) {
        int startLevel = isClient() ? CLIENT_BLOCK_REACH_START_LEVEL : ModConfig.BLOCK_REACH_START_LEVEL.get();
        double maxBonus = isClient() ? CLIENT_BLOCK_REACH_MAX_BONUS : ModConfig.BLOCK_REACH_MAX_BONUS.get();
        
        if (stack <= startLevel || maxBonus <= 0d) {
            return 0d;
        }
        
        int maxStacks = getMaxStacks();
        if (maxStacks <= startLevel) {
            return maxBonus;
        }
        
        double progress = (double) (stack - startLevel) / (double) (maxStacks - startLevel);
        return Mth.clamp(progress, 0d, 1d) * maxBonus;
    }
}
