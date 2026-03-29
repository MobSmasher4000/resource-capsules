package org.mob.resource_capsules;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
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
import org.mob.resource_capsules.screen.screen.*;
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

        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModCreativeModTabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.RESOURCE_GEN_TIER_1_MENU.get(), ResourceGenTier1Screen::new);
            MenuScreens.register(ModMenuTypes.RESOURCE_GEN_TIER_2_MENU.get(), ResourceGenTier2Screen::new);
            MenuScreens.register(ModMenuTypes.RESOURCE_GEN_TIER_3_MENU.get(), ResourceGenTier3Screen::new);
            MenuScreens.register(ModMenuTypes.RESOURCE_GEN_MULTIBLOCK_MENU.get(), ResourceGenMultiblockScreen::new);
            MenuScreens.register(ModMenuTypes.TIER_9001_MENU.get(), Tier9001Screen::new);
            MenuScreens.register(ModMenuTypes.DIMENSIONAL_RESOURCE_GEN_MENU.get(), DimensionalResourceGenScreen::new);
            MenuScreens.register(ModMenuTypes.ENCAPSULATING_TRANSMUTATOR_MENU.get(), EncapsulatingTransmutatorScreen::new);
            MenuScreens.register(ModMenuTypes.CATALYTIC_CONVERTER_MENU.get(), CatalyticConverterScreen::new);
            MenuScreens.register(ModMenuTypes.BIO_RESOURCE_GEN_MENU.get(), BioResourceGenScreen::new);
        }
    }

    public static ResourceLocation resourceLocation(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
