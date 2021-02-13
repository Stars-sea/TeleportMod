package com.github.star_sea.teleport.item;

import com.github.star_sea.teleport.util.NBTHelper;
import com.github.star_sea.teleport.util.Pos;
import com.github.star_sea.teleport.util.PosCache;
import com.github.star_sea.teleport.util.PosContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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

    protected void onBroken(PlayerEntity player) {
        player.addItemStackToInventory(new ItemStack(Items.PAPER));
        player.addItemStackToInventory(ItemManager.ender_pearl_fragment.get().getRandomFragments());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn,
                               List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT nbt = NBTHelper.getSubNBT(stack);
        if (PosCache.hasCache(nbt))
            tooltip.add(PosCache.get(nbt).getText());
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        CompoundNBT nbt = NBTHelper.getSubNBT(stack);

        if (!PosCache.hasCache(nbt)) {
            PosContainer container = new PosContainer(new Pos(playerIn));
            FoodStats foodStats    = playerIn.getFoodStats();

            // 检查是否符合保存条件
            if (foodStats.getFoodLevel() < 6) {
                if (!worldIn.isRemote)
                    playerIn.sendMessage(container.getText().saveFailed(stack), Util.DUMMY_UUID);
                return ActionResult.resultFail(stack);
            }

            PosCache.put(nbt, container);
            foodStats.setFoodLevel(foodStats.getFoodLevel() - 6);

            if (!worldIn.isRemote) playerIn.sendMessage(container.getText().saveSucceed(stack), Util.DUMMY_UUID);
        } else {
            PosContainer container = PosCache.get(nbt);
            if (container.pos.teleport(playerIn).flag) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) == 0)
                    stack.damageItem(1, playerIn, this::onBroken);

                playerIn.addStat(Stats.ITEM_USED.get(this));
                playerIn.getCooldownTracker().setCooldown(this, 40);
                playerIn.sendStatusMessage(container.getText().tpSuccess(), true);
            }
            else {
                playerIn.sendStatusMessage(container.getText().tpFail(), true);
                return ActionResult.resultFail(stack);
            }
        }
        return ActionResult.resultConsume(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        /* 可附魔 无限, 耐久, 经验修补; 但 无限 不可与其他两个并存 */
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0)
            return false;
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 ||
            EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING,    stack) > 0) {
            return Enchantments.INFINITY != enchantment;
        }

        return  Enchantments.MENDING    == enchantment ||
                Enchantments.UNBREAKING == enchantment ||
                Enchantments.INFINITY   == enchantment;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) { return 1; }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ItemManager.ender_pearl_fragment.get();
    }
}
