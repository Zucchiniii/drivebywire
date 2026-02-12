package edn.stratodonut.drivebywire.network;

import edn.stratodonut.drivebywire.DriveByWireMod;
import edn.stratodonut.drivebywire.client.ClientWireNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class WireNetworkFullSyncPacket implements CustomPacketPayload {
    public static final Type<WireNetworkFullSyncPacket> TYPE = new Type<>(DriveByWireMod.getResource("wire_full_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WireNetworkFullSyncPacket> STREAM_CODEC =
            StreamCodec.ofMember(WireNetworkFullSyncPacket::write, WireNetworkFullSyncPacket::new);

    long shipId;
    CompoundTag nbt;

    public WireNetworkFullSyncPacket(long id, CompoundTag nbt) {
        this.shipId = id;
        this.nbt = nbt;
    }

    public WireNetworkFullSyncPacket(RegistryFriendlyByteBuf buffer) {
        this.shipId = buffer.readLong();
        this.nbt = buffer.readNbt();
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(shipId);
        buffer.writeNbt(nbt);
    }

    public static void handle(WireNetworkFullSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player p = Minecraft.getInstance().player;
            if (p == null) return;
            ClientWireNetworkHandler.loadFrom(p.level(), packet.shipId, packet.nbt);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
