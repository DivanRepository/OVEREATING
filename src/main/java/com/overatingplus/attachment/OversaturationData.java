package com.overatingplus.attachment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.overatingplus.ModConfig;
import com.overatingplus.logic.OversaturationLogic;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class OversaturationData {
    private float points;
    private float fatArmor;
    private final Deque<ResourceLocation> recentFoods = new ArrayDeque<>();
    private float lastExhaustionSample;
    private boolean bounceEnabled = true;
    private boolean shockwaveEnabled = true;
    private transient boolean wasOnGround = true;
    private transient double fallMotionY;
    private transient int bounceChain;
    private transient float accumulatedFallDistance;
    private transient int lastAppliedSizeStack = -1;
    private transient boolean sizeEffectActive;
    private transient boolean bounceCooldown = false; // Prevents multiple bounce deductions per landing

    public OversaturationData() {
    }

    private OversaturationData(
            float points,
            float fatArmor,
            List<ResourceLocation> foods,
            float lastExhaustionSample,
            boolean bounceEnabled,
            boolean shockwaveEnabled
    ) {
        this.points = points;
        this.fatArmor = fatArmor;
        this.lastExhaustionSample = lastExhaustionSample;
        this.bounceEnabled = bounceEnabled;
        this.shockwaveEnabled = shockwaveEnabled;
        this.recentFoods.addAll(foods);
    }

    public static final Codec<OversaturationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("points").forGetter(OversaturationData::getPoints),
            Codec.FLOAT.fieldOf("fat_armor").forGetter(OversaturationData::getFatArmor),
            ResourceLocation.CODEC.listOf().fieldOf("recent_foods").forGetter(OversaturationData::recentFoodsList),
            Codec.FLOAT.optionalFieldOf("last_exhaustion", 0f).forGetter(OversaturationData::getLastExhaustionSample),
            Codec.BOOL.optionalFieldOf("bounce_enabled", true).forGetter(OversaturationData::isBounceEnabled),
            Codec.BOOL.optionalFieldOf("shockwave_enabled", true).forGetter(OversaturationData::isShockwaveEnabled)
    ).apply(instance, OversaturationData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OversaturationData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, OversaturationData::getPoints,
            ByteBufCodecs.FLOAT, OversaturationData::getFatArmor,
            ByteBufCodecs.collection(ArrayList::new, ResourceLocation.STREAM_CODEC), OversaturationData::recentFoodsList,
            ByteBufCodecs.FLOAT, OversaturationData::getLastExhaustionSample,
            ByteBufCodecs.BOOL, OversaturationData::isBounceEnabled,
            ByteBufCodecs.BOOL, OversaturationData::isShockwaveEnabled,
            OversaturationData::new
    );

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = OversaturationLogic.clampPoints(points);
        if (this.points <= 0f) {
            clearRecentFoods();
        }
    }

    public void addPoints(float amount) {
        if (amount > 0f) {
            this.points = OversaturationLogic.clampPoints(this.points + amount);
        }
    }

    public float getFatArmor() {
        return fatArmor;
    }

    public void setFatArmor(float fatArmor) {
        float maxHp = ModConfig.MAX_FAT_ARMOR_HP.get().floatValue();
        this.fatArmor = Mth.clamp(fatArmor, 0f, maxHp);
    }

    public float getLastExhaustionSample() {
        return lastExhaustionSample;
    }

    public void setLastExhaustionSample(float lastExhaustionSample) {
        this.lastExhaustionSample = lastExhaustionSample;
    }

    public boolean isBounceEnabled() {
        return bounceEnabled;
    }

    public void setBounceEnabled(boolean bounceEnabled) {
        this.bounceEnabled = bounceEnabled;
    }

    public boolean isShockwaveEnabled() {
        return shockwaveEnabled;
    }

    public void setShockwaveEnabled(boolean shockwaveEnabled) {
        this.shockwaveEnabled = shockwaveEnabled;
    }

    public List<ResourceLocation> recentFoodsList() {
        return new ArrayList<>(recentFoods);
    }

    public boolean hasRecentFood(ResourceLocation foodId) {
        return recentFoods.contains(foodId);
    }

    public float getRecentFoodOverlayProgress(ResourceLocation foodId) {
        if (!recentFoods.contains(foodId)) {
            return 0f;
        }
        int maxSize = ModConfig.FOOD_VARIETY_HISTORY_SIZE.get();
        if (maxSize <= 0) {
            return 0f;
        }
        int size = recentFoods.size();
        int index = 0;
        for (ResourceLocation id : recentFoods) {
            if (id.equals(foodId)) {
                int slotsBehind = (size - 1) - index;
                float minProgress = 1.0f / maxSize;
                return Math.max(minProgress, 1.0f - slotsBehind / (float) maxSize);
            }
            index++;
        }
        return 0f;
    }

    public void addRecentFood(ResourceLocation foodId) {
        recentFoods.remove(foodId);
        recentFoods.addLast(foodId);
        int maxSize = ModConfig.FOOD_VARIETY_HISTORY_SIZE.get();
        while (recentFoods.size() > maxSize) {
            recentFoods.removeFirst();
        }
    }

    public void clearRecentFoods() {
        recentFoods.clear();
    }

    public boolean wasOnGround() {
        return wasOnGround;
    }

    public void setWasOnGround(boolean wasOnGround) {
        this.wasOnGround = wasOnGround;
    }

    public double getFallMotionY() {
        return fallMotionY;
    }

    public void setFallMotionY(double fallMotionY) {
        this.fallMotionY = fallMotionY;
    }

    public int getBounceChain() {
        return bounceChain;
    }

    public void setBounceChain(int bounceChain) {
        this.bounceChain = Math.max(0, bounceChain);
    }

    public void resetBounceChain() {
        this.bounceChain = 0;
        this.fallMotionY = 0;
        this.bounceCooldown = false;
    }

    public float getAccumulatedFallDistance() {
        return accumulatedFallDistance;
    }

    public void setAccumulatedFallDistance(float accumulatedFallDistance) {
        this.accumulatedFallDistance = Math.max(0f, accumulatedFallDistance);
    }

    public int getLastAppliedSizeStack() {
        return lastAppliedSizeStack;
    }

    public void setLastAppliedSizeStack(int lastAppliedSizeStack) {
        this.lastAppliedSizeStack = lastAppliedSizeStack;
    }

    public boolean isSizeEffectActive() {
        return sizeEffectActive;
    }

    public void setSizeEffectActive(boolean sizeEffectActive) {
        this.sizeEffectActive = sizeEffectActive;
    }

    public boolean isBounceCooldown() {
        return bounceCooldown;
    }

    public void setBounceCooldown(boolean bounceCooldown) {
        this.bounceCooldown = bounceCooldown;
    }
}
