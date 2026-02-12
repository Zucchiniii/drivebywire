package edn.stratodonut.drivebywire.network;

import edn.stratodonut.drivebywire.DriveByWireMod;
import edn.stratodonut.drivebywire.blocks.WireNetworkBackupBlockEntity;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Optional;

public class WireLinkNetworksPacket implements CustomPacketPayload {
    public static final Type<WireLinkNetworksPacket> TYPE = new Type<>(DriveByWireMod.getResource("wire_link_networks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WireLinkNetworksPacket> STREAM_CODEC =
            StreamCodec.ofMember(WireLinkNetworksPacket::write, WireLinkNetworksPacket::new);

    long start;
    long end;

    public WireLinkNetworksPacket(BlockPos start, BlockPos end) {
        this.start = start.asLong();
        this.end = end.asLong();
    }

    public WireLinkNetworksPacket(RegistryFriendlyByteBuf buf) {
        this.start = buf.readLong();
        this.end = buf.readLong();
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(this.start);
        buffer.writeLong(this.end);
    }

    public static void handle(WireLinkNetworksPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sender)) return;
            if (sender.level().getBlockEntity(BlockPos.of(packet.start)) instanceof WireNetworkBackupBlockEntity backupBlockEntity) {
                CompoundTag nbt = backupBlockEntity.getWireNetworkBackupData();

                if (!nbt.contains("Network", Tag.TAG_COMPOUND)) return;

                Ship s1 = VSGameUtilsKt.getShipObjectManagingPos(sender.level(), BlockPos.of(packet.start));
                Ship s2 = VSGameUtilsKt.getShipObjectManagingPos(sender.level(), BlockPos.of(packet.end));
                if (s1 instanceof LoadedServerShip ss1 && s2 instanceof LoadedServerShip ss2) {
                    Optional<ShipWireNetworkManager> m1 = ShipWireNetworkManager.get(ss1);
                    Optional<ShipWireNetworkManager> m2 = ShipWireNetworkManager.get(ss2);

                    if (m1.isPresent() && m2.isPresent()) {
                        nbt = nbt.getCompound("Network");
                        if (!nbt.contains(m2.get().getName(), Tag.TAG_COMPOUND)) {
                            sender.displayClientMessage(Component.literal(String.format("No backup to load for %s to %s!", m1.get().getName(), m2.get().getName())).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                            return;
                        }

                        sender.displayClientMessage(Component.literal(String.format("Relinking %s to %s", m1.get().getName(), m2.get().getName())), true);
                        m1.get().linkNetwork(m2.get(), ss2.getId(), nbt.getCompound(m2.get().getName()));
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
