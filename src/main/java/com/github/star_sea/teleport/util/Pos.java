package com.github.star_sea.teleport.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Random;

public final class Pos implements INBTSerializable<CompoundNBT> {
    public WorldContainer world;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public Pos(PlayerEntity player) {
        world   = new WorldContainer(player);
        yaw     = player.getYaw(1F);
        pitch   = player.getPitch(1F);

        Vector3d pos = player.getPositionVec();
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }

    public Pos(CompoundNBT nbt) {
        deserializeNBT(nbt);
    }

    public ServerWorld getWorld() { return world.getWorld(); }

    public void addParticles(World world) {
        Random random = new Random();
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
        player.teleport(world, x, y, z, yaw, pitch);

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

    public IFormattableTextComponent getBackTextComponent() {
        return new StringTextComponent("/back").modifyStyle(style ->
                style.setFormatting(TextFormatting.GREEN)
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/back"))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new TranslationTextComponent("msg.teleport.back"))));
    }

    public IFormattableTextComponent getTpTextComponent() {
        return new StringTextComponent(toSimpleString()).modifyStyle(style ->
                style.setFormatting(TextFormatting.GREEN)
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, getTpCommand()))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new TranslationTextComponent("msg.teleport.back"))));
    }

    public static IFormattableTextComponent getBackTip(Pos pos) {
        return new TranslationTextComponent("msg.teleport.back_tip",
                pos.getBackTextComponent(), pos.getTpTextComponent());
    }

    public static IFormattableTextComponent getTpSuccess(Pos pos) {
        return new TranslationTextComponent("msg.teleport.tp_succeed", pos.getTpTextComponent());
    }

    public static IFormattableTextComponent getTpFail(@Nullable Pos pos) {
        Object pos_s = pos != null ? pos.getTpTextComponent() : "";
        return new TranslationTextComponent("msg.teleport.tp_failed",  pos_s).mergeStyle(TextFormatting.RED);
    }

    public static IFormattableTextComponent getSaveSucceed(Pos pos, ItemStack stack) {
        return new TranslationTextComponent("msg.teleport.save_succeed",
                pos.getTpTextComponent(), stack.getTextComponent());
    }

    public static IFormattableTextComponent getSaveFailed(Pos pos, ItemStack stack) {
        return new TranslationTextComponent("msg.teleport.save_failed",
                pos.toSimpleString(), stack.getTextComponent());
    }

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
        world   = new WorldContainer((StringNBT) nbt.get("world"));
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
