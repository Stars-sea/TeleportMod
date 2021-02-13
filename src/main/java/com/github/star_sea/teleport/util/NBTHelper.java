package com.github.star_sea.teleport.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class NBTHelper {
    public static final String SUB_NBT_KEY = "teleport";

    @Nonnull
    public static CompoundNBT getSubNBT(CompoundNBT root) {
        if (!root.contains(SUB_NBT_KEY, 10))
            root.put(SUB_NBT_KEY, new CompoundNBT());
        return (CompoundNBT) Objects.requireNonNull(root.get(SUB_NBT_KEY));
    }

    @Nonnull
    public static CompoundNBT getSubNBT(ItemStack stack) {
        return getSubNBT(stack.getOrCreateTag());
    }
}
