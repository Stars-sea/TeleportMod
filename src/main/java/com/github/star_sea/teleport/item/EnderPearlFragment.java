package com.github.star_sea.teleport.item;

import com.github.star_sea.teleport.util.Pos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.Explosion;
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
        ItemStack stack = playerIn.getHeldItem(handIn);
        stack.shrink(1);

        Random random = playerIn.getRNG();
        Pos pos = new Pos(playerIn);
        pos.explosion(playerIn, random.nextInt() % 6, false, Explosion.Mode.NONE);
        pos.addParticles(worldIn);

        return ActionResult.resultConsume(stack);
    }

    public ItemStack getRandomFragments() {
        return new ItemStack(this, new Random().nextInt() % 3);
    }
}
