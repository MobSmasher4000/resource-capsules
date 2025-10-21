package org.mob.resource_capsules;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.item.ModItems;
import org.mob.resource_capsules.recipe.ModRecipes;
import org.mob.resource_capsules.screen.ModMenuTypes;
import org.mob.resource_capsules.screen.screen.ResourceGenTier1Screen;
import org.mob.resource_capsules.screen.screen.ResourceGenTier2Screen;
import org.mob.resource_capsules.screen.screen.ResourceGenTier3Screen;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ResourceCapsules.MOD_ID)
public class ResourceCapsules {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "resource_capsules";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public ResourceCapsules() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModCreativeModTabs.register(modEventBus);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            MenuScreens.register(ModMenuTypes.RESOURCE_GEN_TIER_1_MENU.get(), ResourceGenTier1Screen::new);
            MenuScreens.register(ModMenuTypes.RESOURCE_GEN_TIER_2_MENU.get(), ResourceGenTier2Screen::new);
            MenuScreens.register(ModMenuTypes.RESOURCE_GEN_TIER_3_MENU.get(), ResourceGenTier3Screen::new);
        }
    }
}
