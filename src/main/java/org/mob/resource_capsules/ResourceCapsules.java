package org.mob.resource_capsules;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.item.ModItems;
import org.mob.resource_capsules.recipe.ModRecipes;
import org.mob.resource_capsules.screen.ModMenuTypes;
import org.mob.resource_capsules.screen.screen.*;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ResourceCapsules.MOD_ID)
public class ResourceCapsules {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "resource_capsules";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ResourceCapsules(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegisterCapabilities);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Resource_capsules) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
//        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event){
            event.register(ModMenuTypes.RESOURCE_GEN_TIER_1_MENU.get(), ResourceGenTier1Screen::new);
            event.register(ModMenuTypes.RESOURCE_GEN_TIER_2_MENU.get(), ResourceGenTier2Screen::new);
            event.register(ModMenuTypes.RESOURCE_GEN_TIER_3_MENU.get(), ResourceGenTier3Screen::new);
            event.register(ModMenuTypes.RESOURCE_GEN_MULTIBLOCK_MENU.get(), ResourceGenMultiblockScreen::new);
            event.register(ModMenuTypes.TIER_9001_MENU.get(), Tier9001Screen::new);
            event.register(ModMenuTypes.DIMENSIONAL_RESOURCE_GEN_MENU.get(), DimensionalResourceGenScreen::new);
            event.register(ModMenuTypes.ENCAPSULATING_TRANSMUTATOR_MENU.get(), EncapsulatingTransmutatorScreen::new);
            event.register(ModMenuTypes.CATALYTIC_CONVERTER_MENU.get(), CatalyticConverterScreen::new);
            event.register(ModMenuTypes.BIO_RESOURCE_GEN_MENU.get(), BioResourceGenScreen::new);
            event.register(ModMenuTypes.FLUID_GEN_MENU.get(), FluidGenScreen::new);
            event.register(ModMenuTypes.ITEM_OUTPUT_HATCH_MENU.get(), ItemOutputHatchScreen::new);
            event.register(ModMenuTypes.FLUID_OUTPUT_HATCH_MENU.get(), FluidOutputHatchScreen::new);
            event.register(ModMenuTypes.FLUID_GEN_MULTIBLOCK_MENU.get(), FluidGenMultiblockScreen::new);
        }
    }

    public static ResourceLocation resourceLocation(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {

        // Fluid Output Hatch
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FLUID_OUTPUT_HATCH_BE.get(),
                (be,side) ->be.getFluidHandler());

        // Item Output Hatch
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ITEM_OUTPUT_HATCH_BE.get(),
                (be,side) ->be.getItemHandler());

        // Resource gen tier 1
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.RESOURCE_GEN_TIER_1_BE.get(),
                (be,side)->be.getItemHandler(side));

        // Resource gen tier 2
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.RESOURCE_GEN_TIER_2_BE.get(),
                (be,side)->be.getItemHandler(side));

        // Resource gen tier 3
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.RESOURCE_GEN_TIER_3_BE.get(),
                (be,side)->be.getItemHandler(side));

        // Bio Resource Gen
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.BIO_RESOURCE_GEN_BE.get(),
                (be,side)->be.getItemHandler(side));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.BIO_RESOURCE_GEN_BE.get(),
                (be,side)->be.getFluidHandler(side));

        // Catalytic Convertor
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CATALYTIC_CONVERTER_BE.get(),
                (be,side)->be.getItemHandler(side));

        // Dimensional Resource Generator
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DIMENSIONAL_RESOURCE_GEN_BE.get(),
                (be,side)->be.getItemHandler(side));

        // Encapsulating Transmutator
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ENCAPSULATING_TRANSMUTATOR_BE.get(),
                (be,side)->be.getItemHandler(side));

        // Fluid Gen
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FLUID_GEN_BE.get(),
                (be,side)->be.getFluidHandler(side));

        // Tier 9001
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.TIER_9001_BE.get(),
                (be,side)->be.getItemHandler(side));
    }
}
