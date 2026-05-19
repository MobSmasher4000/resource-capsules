package org.mob.resource_capsules.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.network.packet.SetHatchLinkPacket;

@EventBusSubscriber(modid = ResourceCapsules.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModMessages {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");

        registrar.playToServer(
                SetHatchLinkPacket.TYPE,
                SetHatchLinkPacket.STREAM_CODEC,
                SetHatchLinkPacket::handle
        );

    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
}