package com.github.star_sea.teleport.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class ItemManager {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "teleport");

    public static final RegistryObject<Item> ender_paper = ITEMS.register("ender_paper", EnderPaper::new);
    public static final RegistryObject<Item> broken_ender_pearl = ITEMS.register("broken_ender_pearl",
            () -> new Item(new Item.Properties().maxStackSize(64).group(ItemGroup.MATERIALS)));
}
