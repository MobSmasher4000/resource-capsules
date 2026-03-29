package org.mob.resource_capsules.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;
import org.mob.resource_capsules.block.entity.dummy.ResourceGenMultiblockDummyBlockEntity;
import org.mob.resource_capsules.block.entity.resource_gen_tier.ResourceGenTier1BlockEntity;

import javax.annotation.Nullable;

public class ResourceGenMultiblockDummyBlock extends BaseEntityBlock {

    public ResourceGenMultiblockDummyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ResourceGenMultiblockDummyBlockEntity(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);

            if (entity instanceof ResourceGenMultiblockDummyBlockEntity dummy) {
                BlockPos controllerPos = dummy.getControllerPos();

                if (controllerPos != null) {
                    BlockEntity masterEntity = pLevel.getBlockEntity(controllerPos);

                    if (masterEntity instanceof ResourceGenMultiblockBlockEntity controller) {
                        NetworkHooks.openScreen(((ServerPlayer) pPlayer), controller, controllerPos);
                        return InteractionResult.CONSUME;
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {

        // Only run this if the block is actually being destroyed/replaced (not just changing a property)
        if (pState.getBlock() != pNewState.getBlock()) {

            BlockEntity entity = pLevel.getBlockEntity(pPos);

            if (entity instanceof ResourceGenMultiblockDummyBlockEntity dummy) {
                BlockPos controllerPos = dummy.getControllerPos();

                // If this dummy was linked to a machine...
                if (controllerPos != null) {
                    BlockState controllerState = pLevel.getBlockState(controllerPos);

                    // Find the Controller and force it to re-check the pattern!
                    if (controllerState.getBlock() instanceof ResourceGenMultiblockBlock controllerBlock) {
                        controllerBlock.checkForMultiblock(pLevel, controllerPos);
                    }
                }
            }
        }

        // Don't forget to call super so the block actually drops its items and deletes its BlockEntity!
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        if (!pLevel.isClientSide()) {
            // 1. Define a 5x5x5 search area around the casing that was just placed
            BlockPos startPos = pPos.offset(-2, -2, -2);
            BlockPos endPos = pPos.offset(2, 2, 2);

            // 2. Scan the area for any unformed Controllers
            for (BlockPos searchPos : BlockPos.betweenClosed(startPos, endPos)) {
                BlockState state = pLevel.getBlockState(searchPos);

                if (state.getBlock() instanceof ResourceGenMultiblockBlock controllerBlock) {

                    // 3. Force the controller to run its scan!
                    // If this was the final casing block, the machine will instantly form.
                    controllerBlock.checkForMultiblock(pLevel, searchPos);
                }
            }
        }
    }
}