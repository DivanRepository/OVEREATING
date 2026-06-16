package com.overatingplus;

import com.overatingplus.util.FoodAccumulationSource;

import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.comment("Core oversaturation points, drain, and food variety.").push("oversaturation");

        ACCUMULATION_SOURCE = BUILDER
                .comment("Whether oversaturation gains use food nutrition or saturation values.",
                        "Allowed Values: NUTRITION, SATURATION")
                .defineEnum("accumulationSource", FoodAccumulationSource.NUTRITION);

        DRAIN_SPEED_MULTIPLIER = BUILDER
                .comment("Multiplier for oversaturation drain compared to hunger/saturation (1.0 = same speed).",
                        "Default: 1.0", "Range: 1.0 ~ 5.0")
                .defineInRange("drainSpeedMultiplier", 1.0, 1.0, 5.0);

        MAX_OVERSATURATION_STACKS = BUILDER
                .comment("Maximum oversaturation stacks (each stack is 20 points)",
                        "Default: 40", "Range: 1 ~ 100")
                .defineInRange("maxOversaturationStacks", 40, 1, 100);

        SATURATION_MODE_THRESHOLD = BUILDER
                .comment("Minimum saturation while hunger is full, before oversaturation points can be gained from food.",
                        "Does not block the oversaturation HUD or drain — only gates food/placed-food gains.",
                        "Default: 5.0", "Range: 0.0 ~ 20.0")
                .defineInRange("saturationModeThreshold", 5.0, 0.0, 20.0);

        ENABLE_FOOD_VARIETY = BUILDER
                .comment("Require alternating foods while in oversaturation mode.")
                .define("enableFoodVariety", true);

        FOOD_VARIETY_HISTORY_SIZE = BUILDER
                .comment("How many recent foods are remembered for variety checks.",
                        "Default: 18", "Range: 1 ~ 64")
                .defineInRange("foodVarietyHistorySize", 18, 1, 64);

        FOOD_VARIETY_BLACKLIST = BUILDER
                .comment("Item ids or #item tags excluded from food variety (not blocked, not tracked in history).")
                .defineList("foodVarietyBlacklist", List.of("minecraft:ominous_bottle"), obj -> obj instanceof String);

        FOOD_VARIETY_WHITELIST = BUILDER
                .comment("Item ids or #item tags included in food variety.",
                        "If empty, all oversaturation and placeable-block food except blacklist is included.")
                .defineListAllowEmpty("foodVarietyWhitelist", List.of(), obj -> obj instanceof String);

        BUILDER.pop();

        BUILDER.comment("Combat bonuses from oversaturation stacks, including fat armor.").push("combat");

        MAX_FAT_ARMOR_HP = BUILDER.comment("Maximum fat armor HP. Stack N grants N HP, capped here.", "Default: 40", "Range: 1 ~ 100")
                .defineInRange("maxFatArmorHp", 40, 1, 100);

        FAT_ARMOR_REGEN_INTERVAL = BUILDER.comment("Ticks between fat armor regeneration attempts (1 HP costs 1 oversaturation point).", "Default: 20", "Range: 1 ~ 200")
                .defineInRange("fatArmorRegenInterval", 20, 1, 200);

        SLOWDOWN_START_LEVEL = BUILDER.comment("Stack at which movement slowdown begins.", "Default: 1", "Range: 1 ~ 100")
                .defineInRange("slowdownStartLevel", 1, 1, 100);

        SLOWDOWN_BASE_PERCENT = BUILDER.comment("Movement slowdown at the first slowdown stack (0.01 = 1%).", "Default: 0.01", "Range: 0.0 ~ 1.0")
                .defineInRange("slowdownBasePercent", 0.01, 0.0, 1.0);

        SLOWDOWN_PER_LEVEL_PERCENT = BUILDER.comment("Additional slowdown per stack above the start stack (0.01 = 1%).", "Default: 0.01", "Range: 0.0 ~ 0.5")
                .defineInRange("slowdownPerLevelPercent", 0.01, 0.0, 0.5);

        STRENGTH_START_LEVEL = BUILDER.comment("Stack at which attack damage bonus begins.", "Default: 1", "Range: 1 ~ 100")
                .defineInRange("strengthStartLevel", 1, 1, 100);

        STRENGTH_BASE_PERCENT = BUILDER.comment("Attack damage bonus at the first strength stack (0.02 = 2%).", "Default: 0.02", "Range: 0.0 ~ 2.0")
                .defineInRange("strengthBasePercent", 0.02, 0.0, 2.0);

        STRENGTH_PER_LEVEL_PERCENT = BUILDER.comment("Additional attack damage per stack above the start stack (0.02 = 2%).", "Default: 0.02", "Range: 0.0 ~ 2.0")
                .defineInRange("strengthPerLevelPercent", 0.02, 0.0, 2.0);

        KNOCKBACK_RESISTANCE_START_LEVEL = BUILDER.comment("Stack at which knockback resistance begins.", "Default: 1", "Range: 1 ~ 100")
                .defineInRange("knockbackResistanceStartLevel", 1, 1, 100);

        KNOCKBACK_RESISTANCE_BASE = BUILDER.comment("Knockback resistance at the first resistance stack (0.02 = 2%).", "Default: 0.02", "Range: 0.0 ~ 1.0")
                .defineInRange("knockbackResistanceBase", 0.02, 0.0, 1.0);

        KNOCKBACK_RESISTANCE_PER_LEVEL = BUILDER.comment("Additional knockback resistance per stack above the start stack.", "Default: 0.02", "Range: 0.0 ~ 1.0")
                .defineInRange("knockbackResistancePerLevel", 0.02, 0.0, 1.0);

        MELEE_REPULSE_START_LEVEL = BUILDER.comment("Stack at which melee attackers are knocked back.", "Default: 10", "Range: 1 ~ 100")
                .defineInRange("meleeRepulseStartLevel", 10, 1, 100);

        MELEE_REPULSE_BASE = BUILDER.comment("Knockback strength applied to melee attackers at the first repulse stack.", "Default: 0.5", "Range: 0.0 ~ 5.0")
                .defineInRange("meleeRepulseBase", 0.5, 0.0, 5.0);

        MELEE_REPULSE_PER_LEVEL = BUILDER.comment("Additional knockback strength per stack above the repulse start stack.", "Default: 0.03", "Range: 0.0 ~ 5.0")
                .defineInRange("meleeRepulsePerLevel", 0.03, 0.0, 5.0);

        ATTACK_DISTANCE_START_LEVEL = BUILDER.comment("Stack at which attack distance begins to increase.", "Default: 1", "Range: 1 ~ 100")
                .defineInRange("attackDistanceStartLevel", 1, 1, 100);

        ATTACK_DISTANCE_MAX_BONUS = BUILDER.comment("Maximum bonus attack distance at max stacks (blocks).", "Default: 1.0", "Range: 0.0 ~ 5.0")
                .defineInRange("attackDistanceMaxBonus", 1.0, 0.0, 5.0);

        BLOCK_REACH_START_LEVEL = BUILDER.comment("Stack at which block reach distance begins to increase.", "Default: 1", "Range: 1 ~ 100")
                .defineInRange("blockReachStartLevel", 1, 1, 100);

        BLOCK_REACH_MAX_BONUS = BUILDER.comment("Maximum bonus block reach distance at max stacks (blocks).", "Default: 1.0", "Range: 0.0 ~ 5.0")
                .defineInRange("blockReachMaxBonus", 1.0, 0.0, 5.0);

        BUILDER.pop();

        BUILDER.comment("Movement abilities unlocked at high oversaturation stacks.").push("abilities");

        SLIME_BOUNCE_START_LEVEL = BUILDER.comment("Stack at which the player bounces off blocks like slime.", "Default: 10", "Range: 1 ~ 100")
                .defineInRange("slimeBounceStartLevel", 10, 1, 100);

        SLIME_BOUNCE_FACTOR = BUILDER.comment("Base bounce strength on first impact (0.6 = softer than vanilla slime).", "Default: 0.6", "Range: 0.0 ~ 2.0")
                .defineInRange("slimeBounceFactor", 0.6, 0.0, 2.0);

        SLIME_BOUNCE_CHAIN_DAMPING = BUILDER.comment("Each consecutive bounce multiplies strength by this (0.5 = 50% weaker per hop).", "Default: 0.5", "Range: 0.0 ~ 1.0")
                .defineInRange("slimeBounceChainDamping", 0.5, 0.0, 1.0);

        SLIME_BOUNCE_MIN_FALL_SPEED = BUILDER.comment("Minimum downward speed required to bounce (blocks/tick).", "Default: 0.8", "Range: 0.01 ~ 2.0")
                .defineInRange("slimeBounceMinFallSpeed", 0.8, 0.01, 2.0);

        SLIME_BOUNCE_STOP_SPEED = BUILDER.comment("If the next bounce would be weaker than this, the player stops instead (blocks/tick).", "Default: 0.1", "Range: 0.0 ~ 1.0")
                .defineInRange("slimeBounceStopSpeed", 0.1, 0.0, 1.0);

        SHOCKWAVE_START_LEVEL = BUILDER.comment("Stack at which the landing shockwave ability unlocks.", "Default: 25", "Range: 1 ~ 100")
                .defineInRange("shockwaveStartLevel", 25, 1, 100);

        SHOCKWAVE_MIN_FALL_BLOCKS = BUILDER.comment("Minimum fall height to trigger shockwave (mace uses 1.5).", "Default: 5", "Range: 0.5 ~ 50.0")
                .defineInRange("shockwaveMinFallBlocks", 5.0, 0.5, 50.0);

        SHOCKWAVE_RADIUS = BUILDER.comment("Shockwave radius in blocks.", "Default: 3.5", "Range: 1.0 ~ 16.0")
                .defineInRange("shockwaveRadius", 3.5, 1.0, 16.0);

        SHOCKWAVE_KNOCKBACK = BUILDER.comment("Knockback strength applied to entities hit by the shockwave.", "Default: 1.2", "Range: 0.0 ~ 10.0")
                .defineInRange("shockwaveKnockback", 1.2, 0.0, 10.0);

        BUILDER.comment("Oversaturation point costs for using abilities.").push("ability_costs");

        SLIME_BOUNCE_COST = BUILDER.comment("Oversaturation points consumed per slime bounce.", "Default: 5.0", "Range: 0.0 ~ 100.0")
                .defineInRange("slimeBounceCost", 5.0, 0.0, 100.0);

        SHOCKWAVE_COST = BUILDER.comment("Oversaturation points consumed per shockwave trigger.", "Default: 5.0", "Range: 0.0 ~ 100.0")
                .defineInRange("shockwaveCost", 5.0, 0.0, 100.0);

        BUILDER.pop();
        BUILDER.pop();

        BUILDER.comment("Player hitbox growth and visual body scaling.").push("character_size");

        SIZE_START_LEVEL = BUILDER.comment("Stack at which the player hitbox begins growing.", "Default: 1", "Range: 1 ~ 100")
                .defineInRange("sizeStartLevel", 1, 1, 100);

        SIZE_MAX_LEVEL = BUILDER.comment("Stack at which the player reaches maximum hitbox size.", "Default: 40", "Range: 1 ~ 100")
                .defineInRange("sizeMaxLevel", 40, 1, 100);

        SIZE_TARGET_WIDTH = BUILDER.comment("Standing hitbox width at max stack (blocks).", "Default: 1.2", "Range: 0.6 ~ 4.0")
                .defineInRange("sizeTargetWidth", 1.2, 0.6, 4.0);

        SIZE_TARGET_HEIGHT = BUILDER.comment("Standing hitbox height cap at max stack (blocks).", "Default: 1.8", "Range: 1.8 ~ 4.0")
                .defineInRange("sizeTargetHeight", 1.8, 1.8, 4.0);

        SIZE_TORSO_MAX_SCALE = BUILDER.comment("Maximum visual torso scale at max oversaturation stack.", "Default: 2.2", "Range: 1.0 ~ 4.0")
                .defineInRange("sizeTorsoMaxScale", 2.2, 1.0, 4.0);

        SIZE_AXIS_X = BUILDER.comment("Width growth multiplier (1.0 = full growth toward sizeTargetWidth).", "Default: 1.0", "Range: 0.0 ~ 2.0")
                .defineInRange("sizeAxisX", 1.0, 0.0, 2.0);

        SIZE_AXIS_Y = BUILDER.comment("Fraction of sizeTargetHeight growth applied to hitbox height and body model.", "Default: 0.2", "Range: 0.0 ~ 2.0")
                .defineInRange("sizeAxisY", 0.2, 0.0, 2.0);

        SIZE_AXIS_Z = BUILDER.comment("Depth growth multiplier for body model (hitbox width uses X/Z average).", "Default: 1.0", "Range: 0.0 ~ 2.0")
                .defineInRange("sizeAxisZ", 1.0, 0.0, 2.0);

        SIZE_CAMERA_PULLBACK = BUILDER.comment("Extra third-person camera distance at max size.", "Default: 0", "Range: 0.0 ~ 8.0")
                .defineInRange("sizeCameraPullback", 0.0, 0.0, 8.0);

        BUILDER.pop();

        BUILDER.comment("Scales block redstone output mapping.").push("scales");

        SCALES_REDSTONE_MIN_STACK = BUILDER.comment("Minimum oversaturation stack before scales emit any redstone signal.", "Default: 0", "Range: 0 ~ 99")
                .defineInRange("scalesRedstoneMinStack", 0, 0, 99);

        SCALES_REDSTONE_MAX_POWER = BUILDER.comment("Maximum redstone power at max oversaturation stack (0-15).", "Default: 15", "Range: 0 ~ 15")
                .defineInRange("scalesRedstoneMaxPower", 15, 0, 15);

        BUILDER.pop();
    }

    // Fields
    public static final ModConfigSpec.EnumValue<FoodAccumulationSource> ACCUMULATION_SOURCE;
    public static final ModConfigSpec.DoubleValue DRAIN_SPEED_MULTIPLIER;
    public static final ModConfigSpec.IntValue MAX_OVERSATURATION_STACKS;
    public static final ModConfigSpec.DoubleValue SATURATION_MODE_THRESHOLD;
    public static final ModConfigSpec.BooleanValue ENABLE_FOOD_VARIETY;
    public static final ModConfigSpec.IntValue FOOD_VARIETY_HISTORY_SIZE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FOOD_VARIETY_BLACKLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FOOD_VARIETY_WHITELIST;

    public static final ModConfigSpec.IntValue MAX_FAT_ARMOR_HP;
    public static final ModConfigSpec.IntValue FAT_ARMOR_REGEN_INTERVAL;
    public static final ModConfigSpec.IntValue SLOWDOWN_START_LEVEL;
    public static final ModConfigSpec.DoubleValue SLOWDOWN_BASE_PERCENT;
    public static final ModConfigSpec.DoubleValue SLOWDOWN_PER_LEVEL_PERCENT;
    public static final ModConfigSpec.IntValue STRENGTH_START_LEVEL;
    public static final ModConfigSpec.DoubleValue STRENGTH_BASE_PERCENT;
    public static final ModConfigSpec.DoubleValue STRENGTH_PER_LEVEL_PERCENT;
    public static final ModConfigSpec.IntValue KNOCKBACK_RESISTANCE_START_LEVEL;
    public static final ModConfigSpec.DoubleValue KNOCKBACK_RESISTANCE_BASE;
    public static final ModConfigSpec.DoubleValue KNOCKBACK_RESISTANCE_PER_LEVEL;
    public static final ModConfigSpec.IntValue MELEE_REPULSE_START_LEVEL;
    public static final ModConfigSpec.DoubleValue MELEE_REPULSE_BASE;
    public static final ModConfigSpec.DoubleValue MELEE_REPULSE_PER_LEVEL;
    public static final ModConfigSpec.IntValue ATTACK_DISTANCE_START_LEVEL;
    public static final ModConfigSpec.DoubleValue ATTACK_DISTANCE_MAX_BONUS;
    public static final ModConfigSpec.IntValue BLOCK_REACH_START_LEVEL;
    public static final ModConfigSpec.DoubleValue BLOCK_REACH_MAX_BONUS;

    public static final ModConfigSpec.IntValue SLIME_BOUNCE_START_LEVEL;
    public static final ModConfigSpec.DoubleValue SLIME_BOUNCE_FACTOR;
    public static final ModConfigSpec.DoubleValue SLIME_BOUNCE_CHAIN_DAMPING;
    public static final ModConfigSpec.DoubleValue SLIME_BOUNCE_MIN_FALL_SPEED;
    public static final ModConfigSpec.DoubleValue SLIME_BOUNCE_STOP_SPEED;
    public static final ModConfigSpec.IntValue SHOCKWAVE_START_LEVEL;
    public static final ModConfigSpec.DoubleValue SHOCKWAVE_MIN_FALL_BLOCKS;
    public static final ModConfigSpec.DoubleValue SHOCKWAVE_RADIUS;
    public static final ModConfigSpec.DoubleValue SHOCKWAVE_KNOCKBACK;
    public static final ModConfigSpec.DoubleValue SLIME_BOUNCE_COST;
    public static final ModConfigSpec.DoubleValue SHOCKWAVE_COST;

    public static final ModConfigSpec.IntValue SIZE_START_LEVEL;
    public static final ModConfigSpec.IntValue SIZE_MAX_LEVEL;
    public static final ModConfigSpec.DoubleValue SIZE_TARGET_WIDTH;
    public static final ModConfigSpec.DoubleValue SIZE_TARGET_HEIGHT;
    public static final ModConfigSpec.DoubleValue SIZE_TORSO_MAX_SCALE;
    public static final ModConfigSpec.DoubleValue SIZE_AXIS_X;
    public static final ModConfigSpec.DoubleValue SIZE_AXIS_Y;
    public static final ModConfigSpec.DoubleValue SIZE_AXIS_Z;
    public static final ModConfigSpec.DoubleValue SIZE_CAMERA_PULLBACK;

    public static final ModConfigSpec.IntValue SCALES_REDSTONE_MIN_STACK;
    public static final ModConfigSpec.IntValue SCALES_REDSTONE_MAX_POWER;

    public static final ModConfigSpec SPEC = BUILDER.build();
}
