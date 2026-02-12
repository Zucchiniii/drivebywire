package edn.stratodonut.drivebywire.items;

import edn.stratodonut.drivebywire.client.ClientWireNetworkHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class WireItem extends Item {
    public WireItem(Properties p_41383_) {
        super(p_41383_);
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p, InteractionHand p_41434_) {
//        if (p.isCrouching()) {
//            if (p.level().isClientSide) {
//                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientWireNetworkHandler::clearSource);
//            }
//            return InteractionResultHolder.consume(p.getItemInHand(p_41434_));
//        }
//
//        return super.use(p_41432_, p, p_41434_);
//    }
}
