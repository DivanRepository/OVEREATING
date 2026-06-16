package com.overatingplus.event;

import com.overatingplus.ModConfig;
import com.overatingplus.attachment.OversaturationAttachments;
import com.overatingplus.attachment.OversaturationData;
import com.overatingplus.logic.OversaturationLogic;
import com.overatingplus.logic.PlaceableFoodContext;
import com.overatingplus.logic.PlaceableFoodLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public final class PlaceableFoodEvents {

    private PlaceableFoodEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Only handle main hand to avoid double processing
        if (event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND) {
            return;
        }

        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Check if this is a placeable food block
        if (!isPlaceableFood(block, state)) {
            return;
        }

        ResourceLocation foodId = ResourceLocation.fromNamespaceAndPath(
                block.builtInRegistryHolder().key().location().getNamespace(),
                block.builtInRegistryHolder().key().location().getPath()
        );

        com.overatingplus.OveratingPlusMod.LOGGER.debug("[OveratingPlus] Right-click on placeable food: " + foodId);

        // Check food variety - block if player recently ate this
        if (ModConfig.ENABLE_FOOD_VARIETY.get() && OversaturationLogic.isInOversaturationMode(player)) {
            OversaturationData data = OversaturationAttachments.get(player);
            ResourceLocation canonicalId = PlaceableFoodLogic.getCanonicalFoodId(foodId);
            if (data.hasRecentFood(canonicalId)) {
                com.overatingplus.OveratingPlusMod.LOGGER.debug("[OveratingPlus] Blocking recent food: " + foodId + " (canonical: " + canonicalId + ")");
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.translatable("message.overatingplus.want_something_else"), true);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
                return;
            }
        }

        // Store context for the eat callback
        if (!level.isClientSide()) {
            PlaceableFoodContext.begin(player, block);
        }
    }

    private static boolean isPlaceableFood(Block block, BlockState state) {
        // Check if block is a CakeBlock (vanilla)
        if (block instanceof CakeBlock) {
            return true;
        }

        // Check if block has FoodProperties on its item form
        ItemStack stack = new ItemStack(block.asItem());
        if (stack.has(DataComponents.FOOD)) {
            return true;
        }

        // Check if block is in the placeable_oversaturation_blocks tag
        return block.builtInRegistryHolder().is(com.overatingplus.registry.ModTags.PLACEABLE_OVERSATURATION_BLOCKS);
    }
}
