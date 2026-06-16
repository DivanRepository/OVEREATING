package com.overatingplus.logic;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.registry.OveratingPlusSounds;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ShockwaveLogic {
    private ShockwaveLogic() {
    }

    /**
     * Mace-style bonus damage from fall height (no base weapon damage).
     */
    public static float computeFallSmashDamage(float fallBlocks) {
        if (fallBlocks <= ModConfig.SHOCKWAVE_MIN_FALL_BLOCKS.get().floatValue()) {
            return 0f;
        }
        float remaining = fallBlocks;
        float damage = 0f;

        float tier1 = Math.min(remaining, 3f);
        damage += tier1 * 4f;
        remaining -= tier1;

        if (remaining > 0f) {
            float tier2 = Math.min(remaining, 5f);
            damage += tier2 * 2f;
            remaining -= tier2;
        }

        if (remaining > 0f) {
            damage += remaining;
        }

        return damage;
    }

    public static void trigger(Player player, float fallBlocks) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (player.isSpectator() || player.getAbilities().instabuild) {
            return;
        }
        if (player.hasEffect(net.minecraft.world.effect.MobEffects.SLOW_FALLING)) {
            return;
        }

        float damage = computeFallSmashDamage(fallBlocks);
        if (damage <= 0f) {
            return;
        }

        double radius = ModConfig.SHOCKWAVE_RADIUS.get();
        AABB area = player.getBoundingBox().inflate(radius, 1.0D, radius);
        DamageSource source = player.damageSources().playerAttack(player);
        Vec3 center = player.position();

        level.playSound(null, player.blockPosition(), OveratingPlusSounds.SHOCKWAVE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        level.sendParticles(
                ParticleTypes.EXPLOSION,
                center.x,
                center.y + 0.1D,
                center.z,
                10,
                radius * 0.35D,
                0.15D,
                radius * 0.35D,
                0.02D
        );
        level.sendParticles(
                ParticleTypes.POOF,
                center.x,
                center.y + 0.05D,
                center.z,
                24,
                radius * 0.4D,
                0.1D,
                radius * 0.4D,
                0.01D
        );

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != player && entity.isAlive())) {
            double dx = target.getX() - center.x;
            double dz = target.getZ() - center.z;
            double distanceSq = dx * dx + dz * dz;
            if (distanceSq > radius * radius) {
                continue;
            }

            target.hurt(source, damage);
            if (distanceSq < 1.0E-4D) {
                continue;
            }
            double distance = Math.sqrt(distanceSq);
            double knockback = ModConfig.SHOCKWAVE_KNOCKBACK.get();
            target.knockback(knockback, -dx / distance, -dz / distance);
        }

        // Deduct oversaturation points only after successful shockwave
        OversaturationData data = OversaturationAttachments.get(player);
        OversaturationLogic.deductShockwaveCost(player, data);
    }
}
