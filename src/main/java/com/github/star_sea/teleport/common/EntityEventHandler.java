package com.github.star_sea.teleport.common;

import com.github.star_sea.teleport.item.ItemManager;
import com.github.star_sea.teleport.util.Pos;
import com.github.star_sea.teleport.util.PosCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
public final class EntityEventHandler {
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
            player.sendMessage(PosCache.get(nbt).getText().backTip(), Util.DUMMY_UUID);
    }

    @SubscribeEvent
    public static void onEnderPearlImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.ENDER_PEARL)
            InventoryHelper.spawnItemStack(
                    entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(),
                    ItemManager.ender_pearl_fragment.get().getRandomFragments()
            );
    }
}
