package org.mob.resource_capsules.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.screen.menu.ResourceGenTier1Menu;
import org.mob.resource_capsules.screen.menu.ResourceGenTier2Menu;
import org.mob.resource_capsules.screen.menu.ResourceGenTier3Menu;
import org.mob.resource_capsules.screen.menu.Tier9001Menu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ResourceCapsules.MOD_ID);

    public static final RegistryObject<MenuType<ResourceGenTier1Menu>> RESOURCE_GEN_TIER_1_MENU =
            registerMenuType("resource_gen_tier_1_menu", ResourceGenTier1Menu::new);

    public static final RegistryObject<MenuType<ResourceGenTier2Menu>> RESOURCE_GEN_TIER_2_MENU =
            registerMenuType("resource_gen_tier_2_menu", ResourceGenTier2Menu::new);

    public static final RegistryObject<MenuType<ResourceGenTier3Menu>> RESOURCE_GEN_TIER_3_MENU =
            registerMenuType("resource_gen_tier_3_menu", ResourceGenTier3Menu::new);

    public static final RegistryObject<MenuType<Tier9001Menu>> TIER_9001_MENU =
            registerMenuType("tier_9001_menu", Tier9001Menu::new);


    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
