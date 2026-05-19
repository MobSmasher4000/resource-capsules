package org.mob.resource_capsules.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.mob.resource_capsules.block.entity.hatch.FluidOutputHatchBlockEntity;
import org.mob.resource_capsules.block.entity.hatch.ItemOutputHatchBlockEntity;

public record SetHatchLinkPacket(BlockPos blockPos, int linkedSlot) implements CustomPacketPayload {

    public static final Type<SetHatchLinkPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("resource_capsules", "set_hatch_link")
    );

    public static final StreamCodec<FriendlyByteBuf, SetHatchLinkPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> packet.write(buf),
            SetHatchLinkPacket::new
    );

    private SetHatchLinkPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.blockPos());
        buf.writeInt(this.linkedSlot());
    }

    @Override
    public Type<SetHatchLinkPacket> type() {
        return TYPE;
    }

    public static void handle(SetHatchLinkPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (player instanceof ServerPlayer serverPlayer) {
                BlockEntity be = serverPlayer.level().getBlockEntity(payload.blockPos());

                if (be instanceof ItemOutputHatchBlockEntity hatch) {
                    hatch.setLinkedSlot(payload.linkedSlot());
                } else if (be instanceof FluidOutputHatchBlockEntity hatch) {
                    hatch.setLinkedTank(payload.linkedSlot());
                }
            }
        });
    }
}