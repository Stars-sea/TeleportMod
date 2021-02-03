package com.github.star_sea.teleport;

import com.github.star_sea.teleport.item.ItemManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("teleport")
public class Teleport {
    // private static final Logger LOGGER = LogManager.getLogger();

    public Teleport() {
        ItemManager.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
