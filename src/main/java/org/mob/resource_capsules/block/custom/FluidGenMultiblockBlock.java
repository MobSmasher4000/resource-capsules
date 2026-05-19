package org.mob.resource_capsules.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.ModBlocks;
import org.mob.resource_capsules.block.custom.multiblock.ResourceGenStructure;
import org.mob.resource_capsules.block.entity.FluidGenMultiblockBlockEntity;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.hatch.FluidOutputHatchBlockEntity;

import java.util.List;

public class FluidGenMultiblockBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final MapCodec<FluidGenMultiblockBlock> CODEC = simpleCodec(FluidGenMultiblockBlock::new);

    public FluidGenMultiblockBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FORMED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide()) {
            Player player = pPlacer instanceof Player ? (Player) pPlacer : null;
            tryAutoBuild(pLevel, pPos, player);
        }
        checkForMultiblock(pLevel, pPos);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        checkForMultiblock(pLevel, pPos);
    }

    private static BlockPos rotateOffset(BlockPos offset, Direction facing) {
        return switch (facing) {
            case NORTH -> offset;
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            default -> offset;
        };
    }

    public void checkForMultiblock(Level pLevel, BlockPos controllerPos) {
        if (pLevel.isClientSide()) return;

        BlockState currentState = pLevel.getBlockState(controllerPos);
        if (!(currentState.getBlock() instanceof FluidGenMultiblockBlock)) return;
        Direction facing = currentState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        boolean isStructureComplete = true;
        int hatchCount = 0;

        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            BlockPos targetPos = controllerPos.offset(rotateOffset(part.offset, facing));
            BlockState stateAtPos = pLevel.getBlockState(targetPos);

            boolean isCasing = stateAtPos.is(ModBlocks.MACHINE_CASING.get());
            boolean isHatch = stateAtPos.is(ModBlocks.FLUID_OUTPUT_HATCH.get());

            if (isHatch) hatchCount++;

            if (!isCasing && !isHatch) {
                isStructureComplete = false;
                break;
            }
        }

        if (hatchCount > 4) {
            isStructureComplete = false;
        }

        if (isStructureComplete) {
            for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
                BlockPos targetPos = controllerPos.offset(rotateOffset(part.offset, facing));
                BlockEntity be = pLevel.getBlockEntity(targetPos);

                if (be instanceof FluidOutputHatchBlockEntity hatch) {
                    hatch.setControllerPos(controllerPos);
                }
            }

            if (!currentState.getValue(FORMED)) {
                pLevel.setBlock(controllerPos, currentState.setValue(FORMED, true), 3);
            }

        } else {
            if (currentState.getValue(FORMED)) {
                pLevel.setBlock(controllerPos, currentState.setValue(FORMED, false), 3);
            }

            for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
                BlockPos targetPos = controllerPos.offset(rotateOffset(part.offset, facing));
                BlockEntity be = pLevel.getBlockEntity(targetPos);

                if (be instanceof FluidOutputHatchBlockEntity hatch) {
                    hatch.setControllerPos(null);
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(FORMED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof FluidGenMultiblockBlockEntity controller) {
                controller.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof FluidGenMultiblockBlockEntity controller) {
                if (!pState.getValue(FORMED)) {
                    controller.togglePreview();
                    String status = controller.isShowPreview() ? "ON" : "OFF";
                    pPlayer.displayClientMessage(Component.literal("§bMultiblock Preview: §f" + status), true);
                } else {
                    ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider(controller,
                            Component.translatable("block.resource_capsules.fluid_gen_multiblock")), pPos);
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FluidGenMultiblockBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) return null;

        return createTickerHelper(pBlockEntityType, ModBlockEntities.FLUID_GEN_MULTIBLOCK_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.resource_capsules.fluid_gen_multiblock"));
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    private boolean isSpaceClear(Level pLevel, BlockPos testPos, Direction facing, BlockPos ignorePos) {
        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            BlockPos targetPos = testPos.offset(rotateOffset(part.offset, facing));
            if (!pLevel.getBlockState(targetPos).canBeReplaced() && !targetPos.equals(ignorePos)) {
                return false;
            }
        }
        return true;
    }

    private void tryAutoBuild(Level pLevel, BlockPos originalPos, @Nullable Player player) {
        BlockState currentState = pLevel.getBlockState(originalPos);
        Direction facing = currentState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean isCreative = player != null && player.isCreative();

        Item casingItem = ModBlocks.MACHINE_CASING.get().asItem();
        int costAmount = ResourceGenStructure.PARTS.size();

        if (!isCreative && player != null && player.getInventory().countItem(casingItem) < costAmount) {
            player.displayClientMessage(Component.literal("§cMissing materials! Need " + costAmount + "x " + casingItem.getDescription().getString()), true);
            return;
        }

        BlockPos buildPos = originalPos;
        if (!isSpaceClear(pLevel, buildPos, facing, originalPos)) {
            BlockPos shiftedPos = originalPos.above();
            if (isSpaceClear(pLevel, shiftedPos, facing, originalPos)) {
                buildPos = shiftedPos;
            } else {
                if (player != null) player.displayClientMessage(Component.literal("§cNot enough space to auto-build! Clear the area."), true);
                return;
            }
        }

        if (!isCreative && player != null) consumeItems(player, casingItem, costAmount);

        if (!buildPos.equals(originalPos)) {
            pLevel.removeBlock(originalPos, false);
            pLevel.setBlock(buildPos, currentState, 3);
        }

        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            BlockPos targetPos = buildPos.offset(rotateOffset(part.offset, facing));
            pLevel.setBlock(targetPos, ModBlocks.MACHINE_CASING.get().defaultBlockState(), 3);
        }

        if (player != null) player.displayClientMessage(Component.literal("§aMachine Framework Auto-Assembled! Break casings to add Hatches."), true);
    }

    private void consumeItems(Player player, Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                int shrinkBy = Math.min(remaining, stack.getCount());
                stack.shrink(shrinkBy);
                remaining -= shrinkBy;
                if (remaining <= 0) return;
            }
        }
    }
}