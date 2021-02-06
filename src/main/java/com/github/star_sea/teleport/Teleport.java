package com.github.star_sea.teleport;

import com.github.star_sea.teleport.item.ItemManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("teleport")
public class Teleport {
    private static final Logger LOGGER = LogManager.getLogger();

    public Teleport() {
        ItemManager.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static Logger getLOGGER() { return LOGGER; }
}
