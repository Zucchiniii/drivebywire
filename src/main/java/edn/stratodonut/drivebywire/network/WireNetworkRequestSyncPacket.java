package edn.stratodonut.drivebywire.network;

import edn.stratodonut.drivebywire.DriveByWireMod;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WireNetworkRequestSyncPacket implements CustomPacketPayload {
    public static final Type<WireNetworkRequestSyncPacket> TYPE = new Type<>(DriveByWireMod.getResource("wire_request_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WireNetworkRequestSyncPacket> STREAM_CODEC =
            StreamCodec.ofMember(WireNetworkRequestSyncPacket::write, WireNetworkRequestSyncPacket::new);

    long shipId;

    public WireNetworkRequestSyncPacket(long id) {
        shipId = id;
    }

    public WireNetworkRequestSyncPacket(RegistryFriendlyByteBuf buf) {
        shipId = buf.readLong();
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(shipId);
    }

    public static void handle(WireNetworkRequestSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sender)) return;
            Ship s = VSGameUtilsKt.getShipObjectWorld(sender.level()).getLoadedShips().getById(packet.shipId);
            if (s instanceof LoadedServerShip ss) {
                ShipWireNetworkManager.get(ss).ifPresent(m ->
                        PacketDistributor.sendToPlayer(sender, new WireNetworkFullSyncPacket(packet.shipId, m.serialiseToNbt(sender.level(), BlockPos.ZERO, true)))
                );
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
