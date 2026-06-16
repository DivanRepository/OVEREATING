package com.overatingplus.logic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public final class PlaceableFoodContext {
    private static final ThreadLocal<Context> ACTIVE = new ThreadLocal<>();

    private PlaceableFoodContext() {
    }

    public static void begin(Player player, Block block) {
        ACTIVE.set(new Context(player, block));
    }

    public static Context take() {
        Context context = ACTIVE.get();
        ACTIVE.remove();
        return context;
    }

    public static void clear() {
        ACTIVE.remove();
    }

    public record Context(Player player, Block block) {
    }
}
