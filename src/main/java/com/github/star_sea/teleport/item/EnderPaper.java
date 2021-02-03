package com.github.star_sea.teleport.item;

import com.github.star_sea.teleport.util.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
public class EnderPaper extends Item {

    public EnderPaper() {
        super(new Properties().group(ItemGroup.MATERIALS).setNoRepair().maxDamage(100));
    }

    protected static CompoundNBT getChildNBT(ItemStack stack) {
        return stack.getOrCreateChildTag("teleport");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn,
                               List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT nbt = getChildNBT(stack);
        if (PosCache.hasCache(nbt))
            tooltip.add(PosCache.get(nbt).getTpTextComponent());
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
                    playerIn.sendMessage(Pos.getSaveFailed(pos, stack), Util.DUMMY_UUID);
                return ActionResult.resultFail(stack);
            }

            PosCache.put(nbt, pos);
            foodStats.setFoodLevel(foodStats.getFoodLevel() - 6);

            if (!worldIn.isRemote) playerIn.sendMessage(Pos.getSaveSucceed(pos, stack), Util.DUMMY_UUID);
        } else {
            Pos pos = PosCache.get(nbt);
            pos.teleport(playerIn);

            stack.damageItem(1, playerIn, player -> {
                player.sendBreakAnimation(EquipmentSlotType.MAINHAND);
                player.addItemStackToInventory(new ItemStack(Items.PAPER));

                // 10%: 0 个    40%: 1 个    30%: 2 个    20%: 3 个
                float probability = new Random().nextFloat();
                int count;
                if (probability <= 0.1) count = 0;
                else if (0.1 < probability && probability <= 0.5) count = 1;
                else if (0.5 < probability && probability <= 0.8) count = 2;
                else count = 3;
                player.addItemStackToInventory(new ItemStack(ItemManager.broken_ender_pearl.get(), count));
            });

            playerIn.addStat(Stats.ITEM_USED.get(this));
            playerIn.getCooldownTracker().setCooldown(this, 40);
            playerIn.sendStatusMessage(Pos.getTpSuccess(pos), true);

            // 粒子特效
            pos.addParticles(worldIn);
        }

        return ActionResult.resultConsume(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Enchantments.MENDING.getName().equals(enchantment.getName());
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return toRepair.getDamage() > 0 && repair.getItem() == ItemManager.broken_ender_pearl.get();
    }
}
