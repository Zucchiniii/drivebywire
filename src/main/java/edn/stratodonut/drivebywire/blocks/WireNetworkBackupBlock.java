package edn.stratodonut.drivebywire.blocks;

import com.simibubi.create.content.contraptions.actors.AttachedActorBlock;
import com.simibubi.create.foundation.block.IBE;
import com.mojang.serialization.MapCodec;
import edn.stratodonut.drivebywire.WireBlockEntities;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WireNetworkBackupBlock extends HorizontalDirectionalBlock implements IBE<WireNetworkBackupBlockEntity> {
    public static final MapCodec<WireNetworkBackupBlock> CODEC = simpleCodec(WireNetworkBackupBlock::new);

    public WireNetworkBackupBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

//    @Override
//    public void onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
//        super.onPlace(p_60566_, level, pos, p_60569_, p_60570_);
//        if (level.isClientSide) return;
//
//        Ship s = VSGameUtilsKt.getShipManagingPos(level, pos);
//        if (s instanceof ServerShip ss) {
//            if (ShipWireNetworkManager.get(ss).map(m -> m.backupBlockPlaced(level, pos)).orElse(false)) {
//                level.destroyBlock(pos, true);
//            }
//        }
//    }
//
//    @Override
//    public void onRemove(BlockState p_60515_, Level level, BlockPos pos, BlockState p_60518_, boolean p_60519_) {
//        super.onRemove(p_60515_, level, pos, p_60518_, p_60519_);
//        if (level.isClientSide) return;
//
//        Ship s = VSGameUtilsKt.getShipManagingPos(level, pos);
//        if (s instanceof ServerShip ss) {
//            ShipWireNetworkManager.get(ss).ifPresent(m -> m.backupBlockRemoved(pos));
//        }
//    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return IBE.super.newBlockEntity(p_153215_, p_153216_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing;
        if (context.getClickedFace()
                .getAxis()
                .isVertical())
            facing = context.getHorizontalDirection()
                    .getOpposite();
        else {
            BlockState blockState = context.getLevel()
                    .getBlockState(context.getClickedPos()
                            .relative(context.getClickedFace()
                                    .getOpposite()));
            facing = context.getClickedFace();
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public Class<WireNetworkBackupBlockEntity> getBlockEntityClass() {
        return WireNetworkBackupBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WireNetworkBackupBlockEntity> getBlockEntityType() {
        return WireBlockEntities.BACKUP_BLOCK_ENTITY.get();
    }
}
