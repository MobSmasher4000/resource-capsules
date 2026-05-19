package org.mob.resource_capsules.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.screen.menu.*;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ResourceCapsules.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ResourceGenTier1Menu>> RESOURCE_GEN_TIER_1_MENU =
            registerMenuType("resource_gen_tier_1_menu", ResourceGenTier1Menu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ResourceGenTier2Menu>> RESOURCE_GEN_TIER_2_MENU =
            registerMenuType("resource_gen_tier_2_menu", ResourceGenTier2Menu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ResourceGenTier3Menu>> RESOURCE_GEN_TIER_3_MENU =
            registerMenuType("resource_gen_tier_3_menu", ResourceGenTier3Menu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FluidGenMenu>> FLUID_GEN_MENU =
            registerMenuType("fluid_gen_menu", FluidGenMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ResourceGenMultiblockMenu>> RESOURCE_GEN_MULTIBLOCK_MENU =
            registerMenuType("resource_gen_multiblock_menu", ResourceGenMultiblockMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FluidGenMultiblockMenu>> FLUID_GEN_MULTIBLOCK_MENU =
            registerMenuType("fluid_gen_multiblock_menu", FluidGenMultiblockMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<Tier9001Menu>> TIER_9001_MENU =
            registerMenuType("tier_9001_menu", Tier9001Menu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<DimensionalResourceGenMenu>> DIMENSIONAL_RESOURCE_GEN_MENU =
            registerMenuType("dimensional_resource_gen_menu", DimensionalResourceGenMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<EncapsulatingTransmutatorMenu>> ENCAPSULATING_TRANSMUTATOR_MENU =
            registerMenuType("encapsulating_transmutator_menu", EncapsulatingTransmutatorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CatalyticConverterMenu>> CATALYTIC_CONVERTER_MENU =
            registerMenuType("catalytic_converter_menu", CatalyticConverterMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<BioResourceGenMenu>> BIO_RESOURCE_GEN_MENU =
            registerMenuType("bio_resource_gen_menu", BioResourceGenMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<ItemOutputHatchMenu>> ITEM_OUTPUT_HATCH_MENU =
            registerMenuType("item_output_hatch_menu", ItemOutputHatchMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FluidOutputHatchMenu>> FLUID_OUTPUT_HATCH_MENU =
            registerMenuType("fluid_output_hatch_menu", FluidOutputHatchMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
