package edn.stratodonut.drivebywire;

import edn.stratodonut.drivebywire.network.WireAddConnectionPacket;
import edn.stratodonut.drivebywire.network.WireLinkNetworksPacket;
import edn.stratodonut.drivebywire.network.WireNetworkFullSyncPacket;
import edn.stratodonut.drivebywire.network.WireNetworkRequestSyncPacket;
import edn.stratodonut.drivebywire.network.WireRemoveConnectionPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class WirePackets {
    public static final String NETWORK_VERSION = "3";

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);

        registrar.playToClient(WireNetworkFullSyncPacket.TYPE, WireNetworkFullSyncPacket.STREAM_CODEC, WireNetworkFullSyncPacket::handle);

        registrar.playToServer(WireAddConnectionPacket.TYPE, WireAddConnectionPacket.STREAM_CODEC, WireAddConnectionPacket::handle);
        registrar.playToServer(WireRemoveConnectionPacket.TYPE, WireRemoveConnectionPacket.STREAM_CODEC, WireRemoveConnectionPacket::handle);
        registrar.playToServer(WireNetworkRequestSyncPacket.TYPE, WireNetworkRequestSyncPacket.STREAM_CODEC, WireNetworkRequestSyncPacket::handle);
        registrar.playToServer(WireLinkNetworksPacket.TYPE, WireLinkNetworksPacket.STREAM_CODEC, WireLinkNetworksPacket::handle);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
