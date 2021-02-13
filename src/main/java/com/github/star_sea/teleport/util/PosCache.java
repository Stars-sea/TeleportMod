package com.github.star_sea.teleport.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class PosCache {
    public static final String POS_NBT_KEY = "pos";

    public static boolean hasCache(CompoundNBT root) {
        return NBTHelper.getSubNBT(root).contains(POS_NBT_KEY, 10);
    }

    public static void put(CompoundNBT root, PosContainer pos) {
        NBTHelper.getSubNBT(root).put(POS_NBT_KEY, pos.serializeNBT());
    }

    public static void put(CompoundNBT root, String alia, Pos pos) {
        put(root, new PosContainer(alia, pos));
    }

    @Nonnull
    public static PosContainer get(CompoundNBT root) {
        return new PosContainer(NBTHelper.getSubNBT(root).getCompound(POS_NBT_KEY));
    }

    public static void remove(CompoundNBT root) {
        NBTHelper.getSubNBT(root).remove(POS_NBT_KEY);
    }

    public static void remove(PlayerEntity player) {
        remove(player.getPersistentData());
    }
}
