package com.github.star_sea.teleport.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
public final class CommandManager {
    @SubscribeEvent
    public static void onCommandRegistering(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();
        dispatcher.register(BackCommand.builder());
    }
}
