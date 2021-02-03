package com.github.star_sea.teleport.common;

import com.github.star_sea.teleport.util.Pos;
import com.github.star_sea.teleport.util.PosCache;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class PlayerEventHandler {
    @SubscribeEvent
    public static void onPlayerDied(PlayerEvent.Clone event) {
        PlayerEntity player = event.getPlayer();
        if (event.isWasDeath() && !player.getEntityWorld().isRemote)
            PosCache.put(player.getPersistentData(), new Pos(event.getOriginal()));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        CompoundNBT nbt = player.getPersistentData();
        if (!player.getEntityWorld().isRemote && PosCache.hasCache(nbt))
            player.sendMessage(Pos.getBackTip(PosCache.get(nbt)), Util.DUMMY_UUID);
    }
}
