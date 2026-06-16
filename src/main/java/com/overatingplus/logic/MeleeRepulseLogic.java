package com.overatingplus.logic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public final class MeleeRepulseLogic {
    private MeleeRepulseLogic() {
    }

    public static void apply(LivingEntity target, double strength, double dirX, double dirZ) {
        strength *= 1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (strength <= 0.0D) {
            return;
        }

        double distanceSq = dirX * dirX + dirZ * dirZ;
        if (distanceSq < 1.0E-4D) {
            return;
        }

        Vec3 motion = target.getDeltaMovement();
        Vec3 knockback = new Vec3(dirX, 0.0D, dirZ).normalize().scale(strength);
        Vec3 newMotion = new Vec3(
                motion.x / 2.0D - knockback.x,
                target.onGround() ? Math.min(0.4D, motion.y / 2.0D + strength) : motion.y,
                motion.z / 2.0D - knockback.z
        );
        target.setDeltaMovement(newMotion);
        target.hasImpulse = true;

        if (target instanceof ServerPlayer serverPlayer) {
            serverPlayer.hurtMarked = true;
            serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(
                    serverPlayer.getId(),
                    newMotion
            ));
        }
    }
}
