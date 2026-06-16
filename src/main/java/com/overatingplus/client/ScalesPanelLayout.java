package com.overatingplus.client;

public final class ScalesPanelLayout {
    public static final int PANEL_WIDTH = 99;
    public static final int PANEL_HEIGHT = 166;
    public static final int TEXT_X = 32;
    public static final int ROW_HEIGHT = 18;
    public static final int TEXT_COLOR = 0xFFFFFFFF;

    private static final int[] ROW_Y = {19, 39, 59, 79, 99, 119};

    public static final int ABILITY_ROW_Y = 139;
    public static final int ABILITY_SLOT_SIZE = 18;
    public static final int ABILITY_TEXTURE_WIDTH = PANEL_WIDTH;
    public static final int ABILITY_TEXTURE_HEIGHT = PANEL_HEIGHT;
    //private static final int[] ABILITY_SLOT_X = {7, 29, 51, 73};
    private static final int[] ABILITY_SLOT_X = {29, 51};

    private ScalesPanelLayout() {
    }

    public static int getRowTextY(int rowIndex) {
        return ROW_Y[rowIndex] + 5;
    }

    public static int getAbilitySlotX(int slotIndex) {
        return ABILITY_SLOT_X[slotIndex];
    }
}
