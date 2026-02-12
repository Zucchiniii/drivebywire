package edn.stratodonut.drivebywire.network;

import edn.stratodonut.drivebywire.DriveByWireMod;
import edn.stratodonut.drivebywire.WireSounds;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WireAddConnectionPacket implements CustomPacketPayload {
    public static final Type<WireAddConnectionPacket> TYPE = new Type<>(DriveByWireMod.getResource("wire_add_connection"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WireAddConnectionPacket> STREAM_CODEC =
            StreamCodec.ofMember(WireAddConnectionPacket::write, WireAddConnectionPacket::new);

    long shipId;
    long start;
    long end;
    int dir;
    String channel;

    public WireAddConnectionPacket(long shipId, BlockPos start, BlockPos end, Direction dir, String channel) {
        this.shipId = shipId;
        this.start = start.asLong();
        this.end = end.asLong();
        this.dir = dir.get3DDataValue();
        this.channel = channel;
    }

    public WireAddConnectionPacket(RegistryFriendlyByteBuf buf) {
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

    public static void handle(WireAddConnectionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sender)) return;
            Ship s = VSGameUtilsKt.getShipObjectWorld(sender.level()).getLoadedShips().getById(packet.shipId);
            if (s instanceof LoadedServerShip ss) {
                ShipWireNetworkManager m = ShipWireNetworkManager.getOrCreate(ss);
                ShipWireNetworkManager.CONNECTION_RESULT result = m.createConnection(sender.level(), BlockPos.of(packet.start), BlockPos.of(packet.end), Direction.from3DDataValue(packet.dir), packet.channel);
                if (result.isSuccess()) {
                    sender.level().playSound(null, BlockPos.of(packet.end), WireSounds.PLUG_IN.get(), SoundSource.BLOCKS, 1, 1);
                } else {
                    sender.displayClientMessage(Component.literal(result.getDescription()).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
