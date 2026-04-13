package org.mob.resource_capsules.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.mob.resource_capsules.block.entity.hatch.FluidOutputHatchBlockEntity;
import org.mob.resource_capsules.block.entity.hatch.ItemOutputHatchBlockEntity;

import java.util.function.Supplier;

public class SetHatchLinkPacket {
    private final BlockPos blockPos;
    private final int linkedSlot;

    public SetHatchLinkPacket(BlockPos blockPos, int linkedSlot) {
        this.blockPos = blockPos;
        this.linkedSlot = linkedSlot;
    }

    public SetHatchLinkPacket(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.linkedSlot = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeInt(linkedSlot);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            BlockEntity be = player.level().getBlockEntity(blockPos);
            // Verify it's actually the hatch
            if (be instanceof ItemOutputHatchBlockEntity hatch) {
                hatch.setLinkedSlot(linkedSlot);
            }
            if (be instanceof FluidOutputHatchBlockEntity hatch) {
                hatch.setLinkedTank(linkedSlot);
            }
        });
        return true;
    }
}