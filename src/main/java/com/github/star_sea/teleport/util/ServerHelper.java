package com.github.star_sea.teleport.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class ServerHelper {
    @Nullable
    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Nullable
    public static PlayerList getPlayerList() {
        MinecraftServer server = getServer();
        if (server != null) return server.getPlayerList();
        return null;
    }

    @Nullable
    public static ServerPlayerEntity getPlayerByUUID(UUID uuid) {
        PlayerList list = getPlayerList();
        if (list != null) return list.getPlayerByUUID(uuid);
        return null;
    }

    @Nullable
    public static ClientWorld getCurrentClientWorld() {
        return Minecraft.getInstance().world;
    }
}
