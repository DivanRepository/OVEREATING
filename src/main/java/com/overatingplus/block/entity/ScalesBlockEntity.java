package com.overatingplus.block.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.overatingplus.block.ScalesBlock;
import com.overatingplus.logic.ScalesRedstoneLogic;
import com.overatingplus.logic.ScalesWeightLogic;
import com.overatingplus.registry.OveratingPlusBlockEntities;
import com.overatingplus.registry.OveratingPlusBlocks;
import com.overatingplus.registry.OveratingPlusSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ScalesBlockEntity extends BlockEntity {
    private static final int TICKS_PER_TEXTURE_STEP = 2;
    private static final int SOUND_COOLDOWN_TICKS = 12;

    private int signal;
    private int displayWeight;
    private int targetWeight;
    private int animationTicks;
    private int soundCooldown;
    private final Set<UUID> playersOnScale = new HashSet<>();

    public ScalesBlockEntity(BlockPos pos, BlockState state) {
        super(OveratingPlusBlockEntities.SCALES.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ScalesBlockEntity entity) {
        if (!(state.getBlock() instanceof ScalesBlock)) {
            return;
        }

        if (entity.soundCooldown > 0) {
            entity.soundCooldown--;
        }

        int newSignal = 0;
        int newTargetWeight = 0;
        Set<UUID> currentPlayers = new HashSet<>();
        AABB scanBox = new AABB(pos).inflate(0.0625D, 0.5D, 0.0625D);

        for (Player player : level.getEntitiesOfClass(Player.class, scanBox)) {
            BlockPos feet = BlockPos.containing(player.getX(), player.getY() - 0.05D, player.getZ());
            if (!feet.equals(pos)) {
                continue;
            }
            UUID playerId = player.getUUID();
            currentPlayers.add(playerId);
            newSignal = Math.max(newSignal, ScalesRedstoneLogic.getRedstonePower(player));
            newTargetWeight = Math.max(newTargetWeight, ScalesWeightLogic.getTextureLevel(player));

            if (!entity.playersOnScale.contains(playerId)) {
                entity.playWeighSound(level, pos);
            }
        }

        entity.playersOnScale.clear();
        entity.playersOnScale.addAll(currentPlayers);
        entity.targetWeight = newTargetWeight;

        boolean signalChanged = newSignal != entity.signal;
        entity.signal = newSignal;

        entity.tickWeightAnimation(level, pos, state);

        if (signalChanged) {
            level.updateNeighborsAt(pos, OveratingPlusBlocks.SCALES.get());
        }
    }

    private void tickWeightAnimation(Level level, BlockPos pos, BlockState state) {
        if (displayWeight == targetWeight) {
            animationTicks = 0;
            return;
        }

        animationTicks++;
        if (animationTicks < TICKS_PER_TEXTURE_STEP) {
            return;
        }
        animationTicks = 0;

        if (displayWeight < targetWeight) {
            displayWeight++;
        } else {
            displayWeight--;
        }

        int currentWeight = state.getValue(ScalesBlock.WEIGHT);
        if (displayWeight != currentWeight) {
            level.setBlockAndUpdate(pos, state.setValue(ScalesBlock.WEIGHT, displayWeight));
        }
    }

    private void playWeighSound(Level level, BlockPos pos) {
        if (soundCooldown > 0) {
            return;
        }
        level.playSound(null, pos, OveratingPlusSounds.SCALES_WEIGH.get(), SoundSource.BLOCKS, 0.85F, 1.0F);
        soundCooldown = SOUND_COOLDOWN_TICKS;
    }

    public int getSignal() {
        return signal;
    }
}
