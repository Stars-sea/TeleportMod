package com.github.star_sea.teleport.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class WorldContainer implements INBTSerializable<StringNBT> {
    private String location;

    public WorldContainer(RegistryKey<World> worldKey) {
        location = worldKey.getLocation().toString();
    }

    public WorldContainer(World world) { this(world.getDimensionKey()); }

    public WorldContainer(PlayerEntity player) { this(player.getEntityWorld()); }

    public WorldContainer(StringNBT nbt) { deserializeNBT(nbt); }

    @Nonnull
    public ResourceLocation getLocation() { return new ResourceLocation(location); }

    @Nonnull
    public RegistryKey<World> getRegistryKey() {
        return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, getLocation());
    }

    public ServerWorld getWorld() {
        return ServerLifecycleHooks.getCurrentServer().getWorld(getRegistryKey());
    }

    @Nonnull
    public String getWorldName() {
        return getLocation().getPath();
    }

    @Nonnull
    public String getWorldFullName() {
        return getLocation().toString();
    }

    @Nonnull
    @Override
    public StringNBT serializeNBT() {
        return StringNBT.valueOf(location);
    }

    @Override
    public void deserializeNBT(StringNBT nbt) {
        location = nbt.getString();
    }
}