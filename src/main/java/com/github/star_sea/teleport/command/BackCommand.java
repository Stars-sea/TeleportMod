package com.github.star_sea.teleport.command;

import com.github.star_sea.teleport.Teleport;
import com.github.star_sea.teleport.util.PosCache;
import com.github.star_sea.teleport.util.PosContainer;
import com.github.star_sea.teleport.util.PosText;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BackCommand {

    public static int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ServerPlayerEntity player;
        try {
            player = source.asPlayer();
        } catch (CommandSyntaxException e) {
            Teleport.getLOGGER().error(e);
            source.sendErrorMessage(new StringTextComponent(e.getMessage()));
            return -1;
        }

        CompoundNBT nbt = player.getPersistentData();
        if (!PosCache.hasCache(nbt)) {
            player.sendMessage(PosText.BackErrorText, Util.DUMMY_UUID);
            return -2;
        }

        PosContainer container = PosCache.get(nbt);
        if (container.pos.teleport(player).flag) {
            player.sendMessage(container.getText().tpSuccess(), Util.DUMMY_UUID);
            PosCache.remove(player);
            return 0;
        }

        player.sendMessage(container.getText().tpFail(), Util.DUMMY_UUID);
        return 1;
    }

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("back").executes(BackCommand::run);
    }
}
