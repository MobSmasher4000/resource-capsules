package org.mob.resource_capsules.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.renderer.ResourceGenTier1BlockEntityRenderer;
import org.mob.resource_capsules.block.entity.renderer.ResourceGenTier2BlockEntityRenderer;
import org.mob.resource_capsules.block.entity.renderer.ResourceGenTier3BlockEntityRenderer;

@Mod.EventBusSubscriber(modid = ResourceCapsules.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.RESOURCE_GEN_TIER_1_BE.get(), ResourceGenTier1BlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESOURCE_GEN_TIER_2_BE.get(), ResourceGenTier2BlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESOURCE_GEN_TIER_3_BE.get(), ResourceGenTier3BlockEntityRenderer::new);
    }
}
