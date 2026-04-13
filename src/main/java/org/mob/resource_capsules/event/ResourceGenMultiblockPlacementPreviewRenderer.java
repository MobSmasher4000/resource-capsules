package org.mob.resource_capsules.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.custom.multiblock.ResourceGenStructure;

@Mod.EventBusSubscriber(modid = ResourceCapsules.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ResourceGenMultiblockPlacementPreviewRenderer {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(ModBlocks.RESOURCE_GEN_MULTIBLOCK.get().asItem())) {
            return;
        }

        BlockHitResult hitResult = event.getTarget();
        BlockPos targetPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        Direction playerFacing = player.getDirection().getOpposite();
        Level level = player.level();

        // ==========================================
        // THE AUTO-SHIFT HOLOGRA LOGIC
        // ==========================================
        BlockPos renderOrigin = targetPos; // Start by assuming we will build right where we look

        // If the space doesn't fit...
        if (!isSpaceClear(level, renderOrigin, playerFacing, targetPos)) {
            BlockPos shiftedPos = targetPos.above(); // Test one block higher

            // If the higher space fits, shift the hologram up!
            if (isSpaceClear(level, shiftedPos, playerFacing, targetPos)) {
                renderOrigin = shiftedPos;
            }
        }
        // ==========================================

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();

        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        // Translate the matrix to our calculated renderOrigin (which might be shifted up!)
        poseStack.translate(renderOrigin.getX() - camX, renderOrigin.getY() - camY, renderOrigin.getZ() - camZ);

        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            BlockPos rotatedOffset = rotateOffset(part.offset, playerFacing);
            BlockPos renderPos = renderOrigin.offset(rotatedOffset);

            if (level.getBlockState(renderPos).canBeReplaced()) {
                poseStack.pushPose();
                poseStack.translate(rotatedOffset.getX(), rotatedOffset.getY(), rotatedOffset.getZ());
                poseStack.translate(0.1, 0.1, 0.1);
                poseStack.scale(0.8f, 0.8f, 0.8f);

                blockRenderer.renderSingleBlock(
                        part.expectedBlock.get().defaultBlockState(),
                        poseStack,
                        bufferSource,
                        15728880,
                        net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
                );

                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    // Client-side copy of the space checker
    private static boolean isSpaceClear(Level pLevel, BlockPos testPos, Direction facing, BlockPos ignorePos) {
        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            BlockPos targetPos = testPos.offset(rotateOffset(part.offset, facing));
            if (!pLevel.getBlockState(targetPos).canBeReplaced() && !targetPos.equals(ignorePos)) {
                return false;
            }
        }
        return true;
    }

    // Mirrored Rotation Math to match the Controller's physical build direction
    private static BlockPos rotateOffset(BlockPos offset, Direction facing) {
        return switch (facing) {
            case NORTH -> offset;
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            default -> offset;
        };
    }
}