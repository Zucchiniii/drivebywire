package edn.stratodonut.drivebywire;

import edn.stratodonut.drivebywire.compat.LinkedControllerWireServerHandler;
import edn.stratodonut.drivebywire.compat.TweakedControllerWireServerHandler;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Comparator;
import java.util.EnumSet;

public class ServerEvents {
    public ServerEvents() {}

    @SubscribeEvent
    public static void onServerWorldTick(LevelTickEvent.Post event) {
        Level world = event.getLevel();
        if (world.isClientSide)
            return;
        LinkedControllerWireServerHandler.tick(world);
        TweakedControllerWireServerHandler.tick(world);
    }

    @SubscribeEvent
    public static void onBlockUpdate(BlockEvent.NeighborNotifyEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            Ship s = VSGameUtilsKt.getShipObjectManagingPos(level, event.getPos());
            if (s instanceof LoadedServerShip ss) {
                BlockState state = level.getBlockState(event.getPos());
                if (state.isSignalSource()) {
                    int maxSignal = EnumSet.allOf(Direction.class).stream()
                            .map(d -> state.getSignal(level, event.getPos(), d))
                            .max(Comparator.naturalOrder())
                            .orElse(0);
                    ShipWireNetworkManager.get(ss).ifPresent(m -> m.setSource(level, event.getPos(),
                            ShipWireNetworkManager.WORLD_REDSTONE_CHANNEL, maxSignal)
                    );
                }

                for (Direction d : event.getNotifiedSides()) {
                    BlockPos nPos = event.getPos().relative(d);
                    BlockState nState = level.getBlockState(nPos);
                    if (!nState.isSignalSource()) {
                        ShipWireNetworkManager.get(ss).ifPresent(m -> m.setSource(level, nPos,
                                ShipWireNetworkManager.WORLD_REDSTONE_CHANNEL, level.getBestNeighborSignal(nPos))
                        );
                    }
                }
            }
        }
    }
}
