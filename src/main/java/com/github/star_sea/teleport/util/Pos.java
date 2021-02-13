package com.github.star_sea.teleport.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class Pos extends Vector3d implements INBTSerializable<CompoundNBT> {
    public final WorldContainer world;

    public Pos(WorldContainer world, double x, double y, double z) {
        super(x, y, z);
        this.world  = world;
    }

    public Pos(WorldContainer world, Vector3d vector3d) {
        this(world, vector3d.x, vector3d.y, vector3d.z);
    }

    public Pos(Entity entity) {
        this(new WorldContainer(entity), entity.getPositionVec());
    }

    public Pos(CompoundNBT nbt) {
        this(
                new WorldContainer(nbt.getString("world")),
                nbt.getDouble("x"),
                nbt.getDouble("y"),
                nbt.getDouble("z")
        );
    }

    public ServerWorld getWorld() { return world.getWorld(); }

    static int getRandomInt(int absMax) {
        Random random = new Random();
        int    rand   = random.nextInt();
        return random.nextBoolean() ? rand : -rand;
    }

    public Pos getRandomPos(int distance) {
        return (Pos) add(getRandomInt(distance), getRandomInt(distance), getRandomInt(distance));
    }

    public boolean addParticles(BasicParticleType type, int times) {
        ClientWorld world  = ServerHelper.getCurrentClientWorld();
        if (world == null || world.getDimensionKey() != this.world.getRegistryKey()) return false;

        Random random = world.getRandom();
        for (int i = 0; i < times; ++i) {
            world.addParticle(type, x, y + random.nextDouble() * 2.0D, z,
                    random.nextGaussian(), 0.0D, random.nextGaussian());
        }
        return true;
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
        player.teleport(world, x, y, z, player.getYaw(1), player.getPitch(1));

        // 播放声音
        Random random = player.getRNG();
        world.playSound(null, x, y, z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        addParticles(ParticleTypes.PORTAL, 32);

        return TpResult.Succeed;
    }

    @Nonnull
    public Explosion explosion(Entity entity, float radius, boolean causesFire, Explosion.Mode mode) {
        return getWorld().createExplosion(entity, x, y, z, radius, causesFire, mode);
    }

    public String toSimpleString() {
        return String.format("%s: [%s %s %s]", world.getWorldName(), Math.round(x), Math.round(y), Math.round(z));
    }

    public String getTpCommand() {
        return String.format("/execute in %s run tp %s %s %s", world.getWorldFullName(), x, y, z);
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("world", world.serializeNBT());
        nbt.putDouble("x", x);
        nbt.putDouble("y", y);
        nbt.putDouble("z", z);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) { throw new RuntimeException(); }

    public enum TpResult {
        Succeed(true), Failed(false), Unknown(true);

        public final boolean flag;

        TpResult(boolean flag) { this.flag = flag; }
    }
}
