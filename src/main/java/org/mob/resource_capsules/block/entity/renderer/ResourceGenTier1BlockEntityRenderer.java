package org.mob.resource_capsules.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.mob.resource_capsules.block.custom.ResourceGenTier1Block;
import org.mob.resource_capsules.block.entity.ResourceGenTier1BlockEntity;

public class ResourceGenTier1BlockEntityRenderer implements BlockEntityRenderer<ResourceGenTier1BlockEntity> {
    public ResourceGenTier1BlockEntityRenderer(BlockEntityRendererProvider.Context context){

    }
    @Override
    public void render(ResourceGenTier1BlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = pBlockEntity.getRenderStack();

        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 1f, 0.5f);
        // Apply facing rotation based on block state
        if (pBlockEntity.getLevel() != null) {
            Direction facing = pBlockEntity.getBlockState().getValue(ResourceGenTier1Block.FACING);
            float yRot = switch (facing) {
                case NORTH -> 180f;
                case SOUTH -> 0f;
                case WEST -> 90f;
                case EAST -> -90f;
                default -> 0f;
            };
            pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        }


        pPoseStack.scale(0.35f, 0.35f, 0.35f);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(270));

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);
        pPoseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
