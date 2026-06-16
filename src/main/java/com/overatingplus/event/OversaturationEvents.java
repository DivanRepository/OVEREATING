package com.overatingplus.event;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.logic.MeleeRepulseLogic;
import com.overatingplus.logic.OversaturationFoodLogic;
import com.overatingplus.logic.OversaturationLogic;
import com.overatingplus.network.OveratingPlusNetworking;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class OversaturationEvents {
    private OversaturationEvents() {
    }

    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem();
        if (!OversaturationFoodLogic.isOversaturationFood(stack)) {
            return;
        }
        ResourceLocation foodId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!OversaturationFoodLogic.shouldBlockRecentFood(player, foodId)) {
            return;
        }
        event.setCanceled(true);
        if (player.level().isClientSide()) {
            player.stopUsingItem();
        }
    }

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = event.getItem();
        if (!OversaturationFoodLogic.isOversaturationFood(stack)) {
            return;
        }

        ResourceLocation foodId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!OversaturationFoodLogic.shouldBlockRecentFood(player, foodId)) {
            return;
        }

        event.setCanceled(true);
        if (player.level().isClientSide()) {
            player.stopUsingItem();
        } else {
            OversaturationFoodLogic.notifyWantSomethingElse(player);
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        OversaturationFoodLogic.applyFoodGain(player, event.getItem());
    }

    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        OversaturationData data = OversaturationAttachments.get(player);
        if (shouldIgnoreMovementAbilities(player)) {
            resetMovementCharge(data);
            return;
        }
        if (OversaturationLogic.canSlimeBounce(player) && !player.onGround()) {
            double vy = player.getDeltaMovement().y;
            if (vy < -ModConfig.SLIME_BOUNCE_MIN_FALL_SPEED.get() * 0.5D) {
                data.setFallMotionY(Math.min(data.getFallMotionY(), vy));
            }
        }
        if (!player.onGround()) {
            data.setAccumulatedFallDistance(Math.max(data.getAccumulatedFallDistance(), player.fallDistance));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        OversaturationData data = OversaturationAttachments.get(player);
        if (shouldIgnoreMovementAbilities(player)) {
            resetMovementCharge(data);
            OversaturationAttachments.set(player, data);
            return;
        }

        int previousStack = OversaturationLogic.getLevel(data.getPoints());

        boolean onGround = player.onGround();
        if (!onGround) {
            data.setAccumulatedFallDistance(Math.max(data.getAccumulatedFallDistance(), player.fallDistance));
        } else if (!data.wasOnGround()) {
            OversaturationLogic.tryShockwaveOnLand(player, data, data.getAccumulatedFallDistance());
            data.setAccumulatedFallDistance(0f);
        }

        OversaturationLogic.drainFromExhaustion(player, data, player.getFoodData());
        OversaturationLogic.drainFromHungerEffect(player, data);

        data.setPoints(OversaturationLogic.clampPoints(data.getPoints()));

        int stack = OversaturationLogic.getLevel(data.getPoints());
        OversaturationLogic.updateFatArmor(player, data, stack, previousStack);
        OversaturationLogic.clampFatArmorToStack(data, stack);
        OversaturationLogic.regenFatArmor(player, data, stack);
        OversaturationLogic.handleSlimeMovement(player, data);

        if (stack != previousStack) {
            OversaturationLogic.applyStackBonuses(player, stack);
        }
        OversaturationLogic.updatePlayerSize(player, data, stack);
        if (com.overatingplus.logic.OversaturationSizeLogic.shouldApplySize(player) && player.horizontalCollision) {
            com.overatingplus.logic.OversaturationSizeLogic.resolveStuckInBlocks(player);
        }
        OversaturationAttachments.set(player, data);
        player.syncData(OversaturationAttachments.OVERSATURATION.get());
    }

    @SubscribeEvent
    public static void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        resetMovementCharge(OversaturationAttachments.get(event.getEntity()));
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        if (shouldIgnoreMovementAbilities(player)) {
            return;
        }
        if (OversaturationLogic.canSlimeBounce(player)) {
            event.setCanceled(true);
            player.resetFallDistance();
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        if (event.getAmount() <= 0f) {
            return;
        }

        var sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof LivingEntity attacker) || sourceEntity == player) {
            return;
        }
        if (event.getSource().getDirectEntity() != sourceEntity) {
            return;
        }

        int stack = OversaturationLogic.getLevel(OversaturationAttachments.get(player).getPoints());
        double repulse = OversaturationLogic.getMeleeRepulseStrength(stack);
        if (repulse <= 0d) {
            return;
        }

        double dx = player.getX() - attacker.getX();
        double dz = player.getZ() - attacker.getZ();
        MeleeRepulseLogic.apply(attacker, repulse, dx, dz);
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        if (player.isSpectator() || player.getAbilities().instabuild) {
            return;
        }

        OversaturationData data = OversaturationAttachments.get(player);
        float fatArmor = data.getFatArmor();
        if (fatArmor <= 0f) {
            return;
        }

        float damage = event.getNewDamage();
        if (damage <= 0f) {
            return;
        }
        float absorbed = Math.min(fatArmor, damage);
        data.setFatArmor(fatArmor - absorbed);
        event.setNewDamage(damage - absorbed);
        OversaturationAttachments.set(player, data);
        player.syncData(OversaturationAttachments.OVERSATURATION.get());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        if (event.isWasDeath()) {
            OversaturationLogic.resetAfterDeath(player);
            return;
        }
        OversaturationData oldData = event.getOriginal().getData(OversaturationAttachments.OVERSATURATION.get());
        OversaturationAttachments.set(player, copyData(oldData));
        OversaturationLogic.syncPlayerState(player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        OversaturationData data = OversaturationAttachments.get(player);
        data.setPoints(OversaturationLogic.clampPoints(data.getPoints()));
        int stack = OversaturationLogic.getLevel(data.getPoints());
        OversaturationLogic.updateFatArmor(player, data, stack, stack);
        OversaturationLogic.clampFatArmorToStack(data, stack);
        OversaturationLogic.syncFatArmorForStack(player, data, stack);
        OversaturationAttachments.set(player, data);
        OversaturationLogic.syncPlayerState(player);
        OversaturationLogic.updatePlayerSize(player, data, stack);

        // Sync server config to client
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            OveratingPlusNetworking.sendConfigSync(serverPlayer);
        }
    }

    private static boolean shouldIgnoreMovementAbilities(Player player) {
        return player.isSpectator() || player.getAbilities().instabuild;
    }

    private static void resetMovementCharge(OversaturationData data) {
        data.setAccumulatedFallDistance(0f);
        data.setFallMotionY(0);
        data.resetBounceChain();
    }

    private static OversaturationData copyData(OversaturationData source) {
        OversaturationData copy = new OversaturationData();
        copy.setPoints(source.getPoints());
        copy.setFatArmor(source.getFatArmor());
        copy.setBounceEnabled(source.isBounceEnabled());
        copy.setShockwaveEnabled(source.isShockwaveEnabled());
        copy.setLastExhaustionSample(source.getLastExhaustionSample());
        for (ResourceLocation id : source.recentFoodsList()) {
            copy.addRecentFood(id);
        }
        return copy;
    }
}
