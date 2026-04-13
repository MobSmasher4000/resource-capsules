package org.mob.resource_capsules.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.mob.resource_capsules.ResourceCapsules;
import org.mob.resource_capsules.network.packet.SetHatchLinkPacket;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        // Create the network channel
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ResourceCapsules.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Register your Hatch packet
        net.messageBuilder(SetHatchLinkPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetHatchLinkPacket::new)
                .encoder(SetHatchLinkPacket::toBytes)
                .consumerMainThread(SetHatchLinkPacket::handle)
                .add();

        // If you are using any other packets, copy that block above and change the class name!
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}