package org.mob.resource_capsules.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.renderer.*;

@EventBusSubscriber(modid = ResourceCapsules.MOD_ID, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.RESOURCE_GEN_MULTIBLOCK_BE.get(), ResourceGenMultiblockPreviewRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLUID_GEN_MULTIBLOCK_BE.get(), FluidGenMultiblockPreviewRenderer::new);
    }
}
