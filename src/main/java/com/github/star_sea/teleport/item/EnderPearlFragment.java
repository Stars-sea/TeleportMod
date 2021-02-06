package com.github.star_sea.teleport.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class EnderPearlFragment extends Item {
    public EnderPearlFragment() {
        super(new Properties().group(ItemGroup.MATERIALS));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        // TODO: 随机将玩家传送到 30 格以内的地方
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public ItemStack getRandomFragments() {
        return new ItemStack(this, new Random().nextInt() % 4);
    }
}
