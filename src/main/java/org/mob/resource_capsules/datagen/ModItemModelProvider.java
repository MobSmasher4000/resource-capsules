package org.mob.resource_capsules.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ResourceCapsules.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.BIO_CATALYST);
        simpleItem(ModItems.BIO_CAPSULE);

        simpleItem(ModItems.END_CATALYST);
        simpleItem(ModItems.END_CAPSULE);
        simpleItem(ModItems.END_ADVANCED_CAPSULE);

        simpleItem(ModItems.NETHER_CATALYST);
        simpleItem(ModItems.NETHER_CAPSULE);
        simpleItem(ModItems.NETHER_ADVANCED_CAPSULE);

        simpleItem(ModItems.OVERWORLD_CATALYST);
        simpleItem(ModItems.OVERWORLD_CAPSULE);

//        simpleItem(ModItems.TIER_1_MINI_CATALYST);
//        simpleItem(ModItems.TIER_1_MINI_CAPSULE);
//        simpleItem(ModItems.TIER_1_MEDIUM_CATALYST);
        simpleItem(ModItems.TIER_1_MEDIUM_CAPSULE);
        simpleItem(ModItems.TIER_1_LARGE_CATALYST);
        simpleItem(ModItems.TIER_1_LARGE_CAPSULE);

        simpleItem(ModItems.TIER_2_MINI_CATALYST);
//        simpleItem(ModItems.TIER_2_MINI_CAPSULE);
        simpleItem(ModItems.TIER_2_MEDIUM_CATALYST);
        simpleItem(ModItems.TIER_2_MEDIUM_CAPSULE);
        simpleItem(ModItems.TIER_2_LARGE_CATALYST);
        simpleItem(ModItems.TIER_2_LARGE_CAPSULE);

//        simpleItem(ModItems.TIER_3_MINI_CATALYST);
//        simpleItem(ModItems.TIER_3_MINI_CAPSULE);
//        simpleItem(ModItems.TIER_3_MEDIUM_CATALYST);
//        simpleItem(ModItems.TIER_3_MEDIUM_CAPSULE);
        simpleItem(ModItems.TIER_3_LARGE_CATALYST);
        simpleItem(ModItems.TIER_3_LARGE_CAPSULE);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(ResourceCapsules.MOD_ID, "item/" + item.getId().getPath()));
    }
}
