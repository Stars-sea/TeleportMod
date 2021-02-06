package com.github.star_sea.teleport.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public final class Pos implements INBTSerializable<CompoundNBT> {
    public WorldContainer world;
    public double x;
    public double y;
    public double z;
    public float yaw    = -1F;
    public float pitch  = -1F;

    public Pos(WorldContainer worldContainer, Vector3d vector3d) {
        world   = worldContainer;
        x       = vector3d.x;
        y       = vector3d.y;
        z       = vector3d.z;
    }

    public Pos(Entity entity) {
        this(new WorldContainer(entity), entity.getPositionVec());
        yaw     = entity.getYaw(1F);
        pitch   = entity.getPitch(1F);
    }

    public Pos(CompoundNBT nbt) { deserializeNBT(nbt); }

    public ServerWorld getWorld() { return world.getWorld(); }

    public float getYaw(Entity entity) { return yaw == -1F ? entity.getYaw(1F) : yaw; }

    public float getPitch(Entity entity) { return pitch == -1F ? entity.getPitch(1F) : pitch; }

    public void addParticles(World world) {
        Random random = world.getRandom();
        for (int i = 0; i < 32; ++i) {
            world.addParticle(ParticleTypes.PORTAL, x, y + random.nextDouble() * 2.0D, z,
                    random.nextGaussian(), 0.0D, random.nextGaussian());
        }
    }

    public TpResult teleport(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity)
            return teleport((ServerPlayerEntity) player);
        return TpResult.Unknown;
    }

    public TpResult teleport(ServerPlayerEntity player) {
        if (!World.isValid(new BlockPos(x, y, z)))
            return TpResult.Failed;

        ServerWorld world = getWorld();
        player.teleport(world, x, y, z, getYaw(player), getPitch(player));

        // 播放声音
        Random random = player.getRNG();
        world.playSound(null, x, y, z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        return TpResult.Succeed;
    }

    public String toSimpleString() {
        return String.format("%s: [%s %s %s]", world.getWorldName(), Math.round(x), Math.round(y), Math.round(z));
    }

    public String getTpCommand() {
        return String.format("/execute in %s run tp %s %s %s", world.getWorldFullName(), x, y, z);
    }

    @Nonnull
    public PosText getText() { return new PosText(this); }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("world", world.serializeNBT());
        nbt.putDouble("x", x);
        nbt.putDouble("y", y);
        nbt.putDouble("z", z);
        nbt.putFloat("yaw", yaw);
        nbt.putFloat("pitch", pitch);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        world   = new WorldContainer(nbt.getString("world"));
        x       = nbt.getDouble("x");
        y       = nbt.getDouble("y");
        z       = nbt.getDouble("z");
        yaw     = nbt.getFloat("yaw");
        pitch   = nbt.getFloat("pitch");
    }

    public enum TpResult {
        Succeed(true), Failed(false), Unknown(true);

        public final boolean flag;

        TpResult(boolean flag) { this.flag = flag; }
    }
}
