package org.mob.resource_capsules.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.custom.FluidGenMultiblockBlock;
import org.mob.resource_capsules.block.custom.multiblock.ResourceGenStructure;
import org.mob.resource_capsules.block.entity.FluidGenMultiblockBlockEntity;

public class FluidGenMultiblockPreviewRenderer implements BlockEntityRenderer<FluidGenMultiblockBlockEntity> {

    public FluidGenMultiblockPreviewRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FluidGenMultiblockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        BlockState controllerState = pBlockEntity.getBlockState();

        if (controllerState.getBlock() instanceof FluidGenMultiblockBlock &&
                !controllerState.getValue(FluidGenMultiblockBlock.FORMED) &&
                pBlockEntity.isShowPreview()) {

            Direction facing = controllerState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

            pPoseStack.pushPose();

            for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
                BlockPos rotatedOffset = rotateOffset(part.offset, facing);
                BlockPos renderPos = pBlockEntity.getBlockPos().offset(rotatedOffset);

                BlockState stateInWorld = pBlockEntity.getLevel() != null ? pBlockEntity.getLevel().getBlockState(renderPos) : null;

                if (stateInWorld != null) {

                    // Is it a valid block? (Casing or Hatch)
                    boolean isValid = stateInWorld.is(ModBlocks.MACHINE_CASING.get()) || stateInWorld.is(ModBlocks.FLUID_OUTPUT_HATCH.get());

                    // Only render if the block is WRONG (or empty)
                    if (!isValid) {
                        pPoseStack.pushPose();
                        pPoseStack.translate(rotatedOffset.getX(), rotatedOffset.getY(), rotatedOffset.getZ());

                        if (!stateInWorld.canBeReplaced()) {
                            // SCENARIO A: The space is occupied by the WRONG block (e.g., Dirt)
                            // Draw a thick RED error bounding box around it!
                            VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.lines());

                            AABB errorBox = new AABB(0, 0, 0, 1, 1, 1).inflate(0.02D);

                            LevelRenderer.renderLineBox(pPoseStack, vertexConsumer, errorBox, 1.0F, 0.0F, 0.0F, 1.0F);

                        } else {
                            // SCENARIO B: The space is EMPTY air.
                            // Draw the normal 80% ghost block.

                            pPoseStack.translate(0.1, 0.1, 0.1);
                            pPoseStack.scale(0.8f, 0.8f, 0.8f);

                            blockRenderer.renderSingleBlock(
                                    part.expectedBlock.get().defaultBlockState(),
                                    pPoseStack,
                                    pBufferSource,
                                    15728880, // Full bright lighting
                                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
                            );
                        }

                        pPoseStack.popPose();
                    }
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