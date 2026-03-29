package org.mob.resource_capsules.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.mob.resource_capsules.block.custom.multiblock.ResourceGenStructure;
import org.mob.resource_capsules.block.entity.ModBlockEntities;
import org.mob.resource_capsules.block.entity.ResourceGenMultiblockBlockEntity;
import org.mob.resource_capsules.block.entity.dummy.ResourceGenMultiblockDummyBlockEntity;

import java.util.HashMap;
import java.util.Map;

public class ResourceGenMultiblockBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public ResourceGenMultiblockBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FORMED, false));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide()) {
            // Check if the entity that placed it is a Player
            Player player = pPlacer instanceof Player ? (Player) pPlacer : null;

            // Instantly attempt the auto-build!
            tryAutoBuild(pLevel, pPos, player);
        }
        checkForMultiblock(pLevel, pPos);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        checkForMultiblock(pLevel, pPos);
    }

    // Rotates the relative offset to match the Controller's facing direction
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
        if (!(currentState.getBlock() instanceof ResourceGenMultiblockBlock)) {
            return;
        }
        Direction facing = currentState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        boolean isStructureComplete = true;

        // 1. Scan the required offsets
        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {

            // Rotate the offset to match the controller's direction
            BlockPos rotatedOffset = rotateOffset(part.offset, facing);
            BlockPos targetPos = controllerPos.offset(rotatedOffset);

            // If even ONE block is wrong, the machine is broken!
            if (!pLevel.getBlockState(targetPos).is(part.expectedBlock.get())) {
                isStructureComplete = false;
                break;
            }
        }

        // 2. Assemble or Destroy
        if (isStructureComplete) {

            // SUCCESS: Link all the dummy blocks
            for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
                BlockPos targetPos = controllerPos.offset(rotateOffset(part.offset, facing));
                BlockEntity be = pLevel.getBlockEntity(targetPos);

                if (be instanceof ResourceGenMultiblockDummyBlockEntity dummy) {
                    dummy.setControllerPos(controllerPos);
                }
            }

            if (!currentState.getValue(FORMED)) {
                pLevel.setBlock(controllerPos, currentState.setValue(FORMED, true), 3);
            }

        } else {

            // BROKEN: Unlink the dummy blocks
            if (currentState.getValue(FORMED)) {
                pLevel.setBlock(controllerPos, currentState.setValue(FORMED, false), 3);
            }

            for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
                BlockPos targetPos = controllerPos.offset(rotateOffset(part.offset, facing));
                BlockEntity be = pLevel.getBlockEntity(targetPos);

                if (be instanceof ResourceGenMultiblockDummyBlockEntity dummy) {
                    if (controllerPos.equals(dummy.getControllerPos())) {
                        dummy.setControllerPos(null);
                    }
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
            if (blockEntity instanceof ResourceGenMultiblockBlockEntity) {
                ((ResourceGenMultiblockBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof ResourceGenMultiblockBlockEntity controller) {

                // If it is NOT formed, toggle the preview!
                if (!pState.getValue(FORMED)) {
                    controller.togglePreview();

                    // Send a quick chat message so the player knows what happened
                    String status = controller.isShowPreview() ? "ON" : "OFF";
                    pPlayer.displayClientMessage(Component.literal("§bMultiblock Preview: §f" + status), true);
                }
                // If it IS formed, open the GUI normally
                else {
                    NetworkHooks.openScreen(((ServerPlayer) pPlayer), controller, pPos);
                }
//                NetworkHooks.openScreen(((ServerPlayer)pPlayer), (ResourceGenMultiblockBlockEntity)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ResourceGenMultiblockBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.RESOURCE_GEN_MULTIBLOCK_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    // Checks if the 3x3x3 space is empty.
    private boolean isSpaceClear(Level pLevel, BlockPos testPos, Direction facing, BlockPos ignorePos) {
        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            BlockPos targetPos = testPos.offset(rotateOffset(part.offset, facing));

            // If there is a solid block in the way, AND it's not the Controller we are currently trying to move...
            if (!pLevel.getBlockState(targetPos).canBeReplaced() && !targetPos.equals(ignorePos)) {
                return false; // The space is blocked!
            }
        }
        return true; // The space is perfect!
    }

    // 2. THE BUILDER: Handles shifting, cost calculations, and spawning
    private void tryAutoBuild(Level pLevel, BlockPos originalPos, @Nullable Player player) {
        BlockState currentState = pLevel.getBlockState(originalPos);
        Direction facing = currentState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        // Is the player in Creative Mode? (We let them build for free!)
        boolean isCreative = player != null && player.isCreative();

        // ==========================================
        // STEP 1: CALCULATE THE COST
        // ==========================================
        Map<Item, Integer> cost = new HashMap<>();
        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            Item item = part.expectedBlock.get().asItem();
            cost.put(item, cost.getOrDefault(item, 0) + 1);
        }

        // ==========================================
        // STEP 2: PRE-CHECK INVENTORY
        // ==========================================
        if (!isCreative && player != null) {
            for (Map.Entry<Item, Integer> entry : cost.entrySet()) {
                if (player.getInventory().countItem(entry.getKey()) < entry.getValue()) {

                    // Abort the build and tell them exactly what they are missing!
                    player.displayClientMessage(Component.literal("§cMissing materials! Need " + entry.getValue() + "x " + entry.getKey().getDescription().getString()), true);
                    return;
                }
            }
        }

        // ==========================================
        // STEP 3: SPACE CHECKING (Ground or Shifted)
        // ==========================================
        BlockPos buildPos = originalPos;
        if (!isSpaceClear(pLevel, buildPos, facing, originalPos)) {
            BlockPos shiftedPos = originalPos.above();

            if (isSpaceClear(pLevel, shiftedPos, facing, originalPos)) {
                buildPos = shiftedPos;
            } else {
                if (player != null) {
                    player.displayClientMessage(Component.literal("§cNot enough space to auto-build! Clear the area."), true);
                }
                return;
            }
        }

        // ==========================================
        // STEP 4: PAY THE TOLL (Consume the items)
        // ==========================================
        // We only reach this code if they HAVE the items AND the space is clear.
        if (!isCreative && player != null) {
            for (Map.Entry<Item, Integer> entry : cost.entrySet()) {
                consumeItems(player, entry.getKey(), entry.getValue());
            }
        }

        // ==========================================
        // STEP 5: CONSTRUCTION & IGNITION
        // ==========================================
        if (!buildPos.equals(originalPos)) {
            pLevel.removeBlock(originalPos, false);
            pLevel.setBlock(buildPos, currentState, 3);
        }

        for (ResourceGenStructure.Part part : ResourceGenStructure.PARTS) {
            BlockPos targetPos = buildPos.offset(rotateOffset(part.offset, facing));
            pLevel.setBlock(targetPos, part.expectedBlock.get().defaultBlockState(), 3);

            BlockEntity be = pLevel.getBlockEntity(targetPos);
            if (be instanceof ResourceGenMultiblockDummyBlockEntity dummy) {
                dummy.setControllerPos(buildPos);
            }
        }

        pLevel.setBlock(buildPos, currentState.setValue(FORMED, true), 3);
        if (player != null) {
            player.displayClientMessage(Component.literal("§aMachine Auto-Assembled!"), true);
        }
    }

    // 3. THE INVENTORY HELPER: Safely deducts items across multiple stacks
    private void consumeItems(Player player, Item item, int amount) {
        int remaining = amount;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.is(item)) {
                // Figure out how much we can take from this specific stack
                int shrinkBy = Math.min(remaining, stack.getCount());
                stack.shrink(shrinkBy);
                remaining -= shrinkBy;

                // If we've collected everything we need, stop searching the inventory!
                if (remaining <= 0) return;
            }
        }
    }
}
