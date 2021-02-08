package com.github.star_sea.teleport.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public final class ItemManager {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "teleport");

    public static final RegistryObject<EnderPaper> ender_paper = register("ender_paper", EnderPaper::new);
    public static final RegistryObject<EnderPearlFragment> ender_pearl_fragment = register("ender_pearl_fragment", EnderPearlFragment::new);

    private static <T extends Item> RegistryObject<T> register(String id, Supplier<T> item) {
        return ITEMS.register(id, item);
    }
}
