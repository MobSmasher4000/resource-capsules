package org.mob.resource_capsules.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.sound.ModSounds;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ResourceCapsules.MOD_ID);

//    bio
    public static final RegistryObject<Item> BIO_CATALYST = ITEMS.register("bio_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BIO_CAPSULE = ITEMS.register("bio_capsule",
        () -> new Item(new Item.Properties()));

//    end
    public static final RegistryObject<Item> END_CATALYST = ITEMS.register("end_catalyst",
        () -> new Item(new Item.Properties()){
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                pTooltipComponents.add(Component.translatable("tooltip.resource_capsules.end_catalyst"));
                super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
            }
        });

    public static final RegistryObject<Item> END_ADVANCED_CATALYST = ITEMS.register("end_advanced_catalyst",
        () -> new Item(new Item.Properties()){
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                pTooltipComponents.add(Component.translatable("tooltip.resource_capsules.end_catalyst"));
                super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
            }
        });

    public static final RegistryObject<Item> END_CAPSULE = ITEMS.register("end_capsule",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> END_ADVANCED_CAPSULE = ITEMS.register("end_advanced_capsule",
        () -> new Item(new Item.Properties()));

//    nether
    public static final RegistryObject<Item> NETHER_CATALYST = ITEMS.register("nether_catalyst",
            () -> new Item(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                    pTooltipComponents.add(Component.translatable("tooltip.resource_capsules.nether_catalyst"));
                    super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
                }
            });

    public static final RegistryObject<Item> NETHER_ADVANCED_CATALYST = ITEMS.register("nether_advanced_catalyst",
            () -> new Item(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                    pTooltipComponents.add(Component.translatable("tooltip.resource_capsules.nether_catalyst"));
                    super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
                }
            });

    public static final RegistryObject<Item> NETHER_CAPSULE = ITEMS.register("nether_capsule",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> NETHER_ADVANCED_CAPSULE = ITEMS.register("nether_advanced_capsule",
            () -> new Item(new Item.Properties()));

//    overworld
    public static final RegistryObject<Item> OVERWORLD_CATALYST = ITEMS.register("overworld_catalyst",
            () -> new Item(new Item.Properties()){
                @Override
                public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                    pTooltipComponents.add(Component.translatable("tooltip.resource_capsules.overworld_catalyst"));
                    super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
                }
            });

    public static final RegistryObject<Item> OVERWORLD_CAPSULE = ITEMS.register("overworld_capsule",
            () -> new Item(new Item.Properties()));


//    tier 1
    public static final RegistryObject<Item> TIER_1_MINI_CATALYST = ITEMS.register("tier_1_mini_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_1_MINI_CAPSULE = ITEMS.register("tier_1_mini_capsule",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_1_MEDIUM_CATALYST = ITEMS.register("tier_1_medium_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_1_MEDIUM_CAPSULE = ITEMS.register("tier_1_medium_capsule",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_1_LARGE_CATALYST = ITEMS.register("tier_1_large_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_1_LARGE_CAPSULE = ITEMS.register("tier_1_large_capsule",
            () -> new Item(new Item.Properties()));

//    tier 2
    public static final RegistryObject<Item> TIER_2_MINI_CATALYST = ITEMS.register("tier_2_mini_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_2_MINI_CAPSULE = ITEMS.register("tier_2_mini_capsule",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_2_MEDIUM_CATALYST = ITEMS.register("tier_2_medium_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_2_MEDIUM_CAPSULE = ITEMS.register("tier_2_medium_capsule",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_2_LARGE_CATALYST = ITEMS.register("tier_2_large_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_2_LARGE_CAPSULE = ITEMS.register("tier_2_large_capsule",
            () -> new Item(new Item.Properties()));

//    tier 3
    public static final RegistryObject<Item> TIER_3_MINI_CATALYST = ITEMS.register("tier_3_mini_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_3_MINI_CAPSULE = ITEMS.register("tier_3_mini_capsule",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_3_MEDIUM_CATALYST = ITEMS.register("tier_3_medium_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_3_MEDIUM_CAPSULE = ITEMS.register("tier_3_medium_capsule",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_3_LARGE_CATALYST = ITEMS.register("tier_3_large_catalyst",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TIER_3_LARGE_CAPSULE = ITEMS.register("tier_3_large_capsule",
            () -> new Item(new Item.Properties()));

    // music disc
    public static final RegistryObject<Item> STELLAR_ODYSSEY_MUSIC_DISC = ITEMS.register("stellar_odyssey_music_disc",
            () -> new RecordItem(6, ModSounds.STELLAR_ODYSSEY, new Item.Properties().stacksTo(1), 4480));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
