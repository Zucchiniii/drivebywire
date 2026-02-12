package edn.stratodonut.drivebywire.blocks;

import edn.stratodonut.drivebywire.client.ClientWireNetworkHandler;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;

public class WireNetworkBackupBlockEntity extends BlockEntity {
    private CompoundTag pendingBackupData;

    public WireNetworkBackupBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    protected void saveAdditional(CompoundTag p_187471_, HolderLookup.Provider registries) {
        super.saveAdditional(p_187471_, registries);
        // If level is clientside, then we still want to save because schematics save on client thread, which is such a mess
        if (level == null) return;

        Ship s = VSGameUtilsKt.getShipObjectManagingPos(level, this.getBlockPos());
        if (s instanceof LoadedServerShip ss) {
            if (pendingBackupData == null) pendingBackupData = new CompoundTag();
            ShipWireNetworkManager.get(ss).ifPresent(
                    m -> pendingBackupData.merge(m.serialiseToNbt(level, this.getBlockPos()))
            );
        } else if (s != null && level.isClientSide) {
            List<ShipWireNetworkManager> t = new ArrayList<>();
            t.add(ClientWireNetworkHandler.getClientManagers(s.getId()));
            if (t.size() == 1 && t.get(0) != null) {
                p_187471_.put("WireNetwork", t.get(0).serialiseToNbt(level, this.getBlockPos()));
            }
        }

        if (pendingBackupData != null) p_187471_.put("WireNetwork", pendingBackupData);
    }

    @Override
    protected void loadAdditional(CompoundTag p_155245_, HolderLookup.Provider registries) {
        super.loadAdditional(p_155245_, registries);
        if (!p_155245_.contains("WireNetwork", Tag.TAG_COMPOUND)) return;
        if (this.level != null && VSGameUtilsKt.getShipObjectManagingPos(this.level, this.getBlockPos()) instanceof LoadedServerShip ss) {
            ShipWireNetworkManager.loadIfNotExists(ss, this.level, p_155245_.getCompound("WireNetwork"),
                    this.getBlockPos(), Rotation.NONE);
        }
        pendingBackupData = p_155245_.getCompound("WireNetwork");
    }

    public CompoundTag getWireNetworkBackupData() {
        if (this.level == null) {
            return pendingBackupData == null ? new CompoundTag() : pendingBackupData.copy();
        }
        CompoundTag tag = saveWithoutMetadata(this.level.registryAccess());
        return tag.getCompound("WireNetwork");
    }
}
