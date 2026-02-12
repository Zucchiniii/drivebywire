package edn.stratodonut.drivebywire.network;

import edn.stratodonut.drivebywire.DriveByWireMod;
import edn.stratodonut.drivebywire.WireSounds;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WireRemoveConnectionPacket implements CustomPacketPayload {
    public static final Type<WireRemoveConnectionPacket> TYPE = new Type<>(DriveByWireMod.getResource("wire_remove_connection"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WireRemoveConnectionPacket> STREAM_CODEC =
            StreamCodec.ofMember(WireRemoveConnectionPacket::write, WireRemoveConnectionPacket::new);

    long shipId;
    long start;
    long end;
    int dir;
    String channel;

    public WireRemoveConnectionPacket(long shipId, BlockPos start, BlockPos end, Direction dir, String channel) {
        this.shipId = shipId;
        this.start = start.asLong();
        this.end = end.asLong();
        this.dir = dir.get3DDataValue();
        this.channel = channel;
    }

    public WireRemoveConnectionPacket(RegistryFriendlyByteBuf buf) {
        this.shipId = buf.readLong();
        this.start = buf.readLong();
        this.end = buf.readLong();
        this.dir = buf.readInt();
        this.channel = buf.readUtf();
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(shipId);
        buffer.writeLong(start);
        buffer.writeLong(end);
        buffer.writeInt(dir);
        buffer.writeUtf(channel);
    }

    public static void handle(WireRemoveConnectionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sender)) return;
            Ship s = VSGameUtilsKt.getShipObjectWorld(sender.level()).getLoadedShips().getById(packet.shipId);
            if (s instanceof LoadedServerShip ss) {
                ShipWireNetworkManager.get(ss).ifPresent(m -> {
                    m.removeConnection(sender.level(), BlockPos.of(packet.start), BlockPos.of(packet.end), Direction.from3DDataValue(packet.dir), packet.channel);
                    sender.level().playSound(null, BlockPos.of(packet.end), WireSounds.PLUG_OUT.get(), SoundSource.BLOCKS, 1, 1);
                });
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
