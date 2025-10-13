package org.mob.resource_capsules;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.item.ModItems;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ResourceCapsules.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CAPSULE_TAB = CREATIVE_MODE_TABS.register("capsule_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.NETHER_CAPSULE.get()))
                    .title(Component.translatable("creative_tab.capsule_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.BIO_CAPSULE.get());

                        pOutput.accept(ModItems.NETHER_CAPSULE.get());
                        pOutput.accept(ModItems.NETHER_ADVANCED_CAPSULE.get());

                        pOutput.accept(ModItems.END_CAPSULE.get());
                        pOutput.accept(ModItems.END_ADVANCED_CAPSULE.get());

                        pOutput.accept(ModItems.OVERWORLD_CAPSULE.get());

                        pOutput.accept(ModItems.TIER_1_MINI_CAPSULE.get());
                        pOutput.accept(ModItems.TIER_2_MINI_CAPSULE.get());
                        pOutput.accept(ModItems.TIER_3_MINI_CAPSULE.get());

                        pOutput.accept(ModItems.TIER_1_MEDIUM_CAPSULE.get());
                        pOutput.accept(ModItems.TIER_2_MEDIUM_CAPSULE.get());
                        pOutput.accept(ModItems.TIER_3_MEDIUM_CAPSULE.get());

                        pOutput.accept(ModItems.TIER_1_LARGE_CAPSULE.get());
                        pOutput.accept(ModItems.TIER_2_LARGE_CAPSULE.get());
                        pOutput.accept(ModItems.TIER_3_LARGE_CAPSULE.get());

                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> CATALYST_TAB = CREATIVE_MODE_TABS.register("catalyst_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BIO_CATALYST.get()))
                    .title(Component.translatable("creative_tab.catalyst_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.BIO_CATALYST.get());

                        pOutput.accept(ModItems.NETHER_CATALYST.get());

                        pOutput.accept(ModItems.END_CATALYST.get());

                        pOutput.accept(ModItems.OVERWORLD_CATALYST.get());

                        pOutput.accept(ModItems.TIER_1_MINI_CATALYST.get());
                        pOutput.accept(ModItems.TIER_2_MINI_CATALYST.get());
                        pOutput.accept(ModItems.TIER_3_MINI_CATALYST.get());

                        pOutput.accept(ModItems.TIER_1_MEDIUM_CATALYST.get());
                        pOutput.accept(ModItems.TIER_2_MEDIUM_CATALYST.get());
                        pOutput.accept(ModItems.TIER_3_MEDIUM_CATALYST.get());

                        pOutput.accept(ModItems.TIER_1_LARGE_CATALYST.get());
                        pOutput.accept(ModItems.TIER_2_LARGE_CATALYST.get());
                        pOutput.accept(ModItems.TIER_3_LARGE_CATALYST.get());

                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> MISC_TAB = CREATIVE_MODE_TABS.register("misc_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BIO_CATALYST.get()))
                    .title(Component.translatable("creative_tab.misc_tab"))
                    .displayItems((pParameters, pOutput) -> {
//                        pOutput.accept(ModItems.SOULLESS_EGG.get());

                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
