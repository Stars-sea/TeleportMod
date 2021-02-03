package com.github.star_sea.teleport.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class PosCache {
    public static final String SUB_NBT_KEY = "teleport";
    public static final String POS_NBT_KEY = "pos";

    public static CompoundNBT getSubNBT(CompoundNBT root) {
        if (!root.contains(SUB_NBT_KEY, 10))
            root.put(SUB_NBT_KEY, new CompoundNBT());
        return (CompoundNBT) root.get(SUB_NBT_KEY);
    }

    public static boolean hasCache(CompoundNBT root) {
        return getSubNBT(root).contains(POS_NBT_KEY, 10);
    }

    public static void put(CompoundNBT root, Pos pos) {
        getSubNBT(root).put(POS_NBT_KEY, pos.serializeNBT());
    }

    @Nonnull
    public static Pos get(CompoundNBT root) {
        return new Pos(getSubNBT(root).getCompound(POS_NBT_KEY));
    }

    public static void remove(CompoundNBT root) {
        getSubNBT(root).remove(SUB_NBT_KEY);
    }

    public static void remove(PlayerEntity player) {
        remove(player.getPersistentData());
    }
}
