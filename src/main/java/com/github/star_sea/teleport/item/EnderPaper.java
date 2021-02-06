package com.github.star_sea.teleport.item;

import com.github.star_sea.teleport.util.Pos;
import com.github.star_sea.teleport.util.PosCache;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class EnderPaper extends Item {

    public EnderPaper() {
        super(new Properties().group(ItemGroup.MATERIALS).setNoRepair().maxDamage(100));
    }

    @Nonnull
    protected static CompoundNBT getChildNBT(ItemStack stack) {
        return stack.getOrCreateChildTag("teleport");
    }

    protected void onBroken(PlayerEntity player) {
        player.addItemStackToInventory(new ItemStack(Items.PAPER));
        player.addItemStackToInventory(ItemManager.ender_pearl_fragment.get().getRandomFragments());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn,
                               List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT nbt = getChildNBT(stack);
        if (PosCache.hasCache(nbt))
            tooltip.add(PosCache.get(nbt).getText());
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        CompoundNBT nbt = getChildNBT(stack);

        if (!PosCache.hasCache(nbt)) {
            Pos pos = new Pos(playerIn);
            FoodStats foodStats = playerIn.getFoodStats();

            // 检查是否符合保存条件
            if (foodStats.getFoodLevel() < 6) {
                if (!worldIn.isRemote)
                    playerIn.sendMessage(pos.getText().saveFailed(stack), Util.DUMMY_UUID);
                return ActionResult.resultFail(stack);
            }

            PosCache.put(nbt, pos);
            foodStats.setFoodLevel(foodStats.getFoodLevel() - 6);

            if (!worldIn.isRemote) playerIn.sendMessage(pos.getText().saveSucceed(stack), Util.DUMMY_UUID);
        } else {
            Pos pos = PosCache.get(nbt);
            if (pos.teleport(playerIn).flag) {
                stack.damageItem(1, playerIn, this::onBroken);

                playerIn.addStat(Stats.ITEM_USED.get(this));
                playerIn.getCooldownTracker().setCooldown(this, 40);
                playerIn.sendStatusMessage(pos.getText().tpSuccess(), true);
                pos.addParticles(worldIn);
            }
            else return ActionResult.resultFail(stack);
        }
        return ActionResult.resultConsume(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Enchantments.MENDING.getName().equals(enchantment.getName());
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ItemManager.ender_pearl_fragment.get();
    }
}
