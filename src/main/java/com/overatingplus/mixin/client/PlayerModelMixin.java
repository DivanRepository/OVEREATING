package com.overatingplus.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.logic.OversaturationLogic;
import com.overatingplus.logic.OversaturationSizeLogic;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends Player> {
    private static final float ARM_SPREAD_FACTOR = 4.0F;
    private static final float LEG_SPREAD_FACTOR = 2.2F;

    @Shadow
    @Final
    private ModelPart jacket;

    @Shadow
    @Final
    private ModelPart leftSleeve;

    @Shadow
    @Final
    private ModelPart rightSleeve;

    @Shadow
    @Final
    private ModelPart leftPants;

    @Shadow
    @Final
    private ModelPart rightPants;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void overatingplus$scaleTorso(net.minecraft.world.entity.LivingEntity entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        HumanoidModel<?> humanoid = overatingplus$humanoid();
        ModelPart body = humanoid.body;
        resetTorsoScale(body);
        if (!OversaturationSizeLogic.shouldApplySize(player)) {
            return;
        }
        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        float xScale = OversaturationSizeLogic.getTorsoAxisScale(stack, ModConfig.SIZE_AXIS_X.get().floatValue());
        float yScale = OversaturationSizeLogic.getTorsoAxisScale(stack, ModConfig.SIZE_AXIS_Y.get().floatValue());
        float zScale = OversaturationSizeLogic.getTorsoAxisScale(stack, ModConfig.SIZE_AXIS_Z.get().floatValue());
        applyTorsoScale(body, xScale, yScale, zScale);
        spreadLimbs(humanoid, xScale);
    }

    private HumanoidModel<?> overatingplus$humanoid() {
        return (HumanoidModel<?>) (Object) this;
    }

    private void resetTorsoScale(ModelPart body) {
        body.xScale = 1.0F;
        body.yScale = 1.0F;
        body.zScale = 1.0F;
        jacket.xScale = 1.0F;
        jacket.yScale = 1.0F;
        jacket.zScale = 1.0F;
    }

    private void applyTorsoScale(ModelPart body, float xScale, float yScale, float zScale) {
        body.xScale = xScale;
        body.yScale = yScale;
        body.zScale = zScale;
        jacket.xScale = xScale;
        jacket.yScale = yScale;
        jacket.zScale = zScale;
    }

    private void spreadLimbs(HumanoidModel<?> humanoid, float xScale) {
        float armSpread = (xScale - 1.0F) * ARM_SPREAD_FACTOR;
        float legSpread = (xScale - 1.0F) * LEG_SPREAD_FACTOR;

        humanoid.rightArm.x -= armSpread;
        humanoid.leftArm.x += armSpread;
        rightSleeve.x -= armSpread;
        leftSleeve.x += armSpread;

        humanoid.rightLeg.x -= legSpread;
        humanoid.leftLeg.x += legSpread;
        rightPants.x -= legSpread;
        leftPants.x += legSpread;
    }
}
