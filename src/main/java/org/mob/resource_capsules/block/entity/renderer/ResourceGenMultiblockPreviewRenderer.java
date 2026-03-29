package org.mob.resource_capsules.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.mob.resource_capsules.block.custom.ResourceGenMultiblockBlock;
import org.mob.resource_capsules.block.custom.multiblock.ResourceGenStructure;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;

public class ResourceGenMultiblockPreviewRenderer implements BlockEntityRenderer<ResourceGenMultiblockBlockEntity> {

    public ResourceGenMultiblockPreviewRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ResourceGenMultiblockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        BlockState controllerState = pBlockEntity.getBlockState();

        // ONLY render if it is an unformed controller AND the player toggled the preview ON!
        if (controllerState.getBlock() instanceof ResourceGenMultiblockBlock &&
                !controllerState.getValue(ResourceGenMultiblockBlock.FORMED) &&
                pBlockEntity.isShowPreview()) {

            Direction facing = controllerState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

            pPoseStack.pushPose();

            for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
                BlockPos rotatedOffset = rotateOffset(part.offset, facing);
                BlockPos renderPos = pBlockEntity.getBlockPos().offset(rotatedOffset);

                // Check if the world space is empty before drawing the ghost block
                if (pBlockEntity.getLevel() != null && pBlockEntity.getLevel().getBlockState(renderPos).canBeReplaced()) {

                    pPoseStack.pushPose();

                    // Move out to the specific coordinate
                    pPoseStack.translate(rotatedOffset.getX(), rotatedOffset.getY(), rotatedOffset.getZ());

                    // Shrink it to 80% size so it looks like a hologram
                    pPoseStack.translate(0.1, 0.1, 0.1);
                    pPoseStack.scale(0.8f, 0.8f, 0.8f);

                    // Draw the block!
                    blockRenderer.renderSingleBlock(
                            part.expectedBlock.get().defaultBlockState(),
                            pPoseStack,
                            pBufferSource,
                            15728880, // Full bright lighting
                            net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
                    );

                    pPoseStack.popPose();
                }
            }
            pPoseStack.popPose();
        }
    }

    private BlockPos rotateOffset(BlockPos offset, Direction facing) {
        return switch (facing) {
            case NORTH -> offset;
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            default -> offset;
        };
    }
}