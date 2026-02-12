package edn.stratodonut.drivebywire.mixin.compat;

import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerInputPacket;
import edn.stratodonut.drivebywire.compat.LinkedControllerWireServerHandler;
import edn.stratodonut.drivebywire.util.HubItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

@Mixin(LinkedControllerInputPacket.class)
public abstract class MixinLinkedControllerInputPacket {
    private Collection<Integer> findActivatedButtons() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!Collection.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof Collection<?> collection) {
                    Collection<Integer> buttons = new ArrayList<>();
                    for (Object element : collection) {
                        if (element instanceof Integer button) {
                            buttons.add(button);
                        }
                    }
                    if (!buttons.isEmpty() || field.getName().toLowerCase().contains("button")) {
                        return buttons;
                    }
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return java.util.List.of();
    }

    private boolean findPressState() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType() != boolean.class) {
                continue;
            }
            String name = field.getName().toLowerCase();
            if (!(name.contains("press") || name.contains("down") || name.contains("active"))) {
                continue;
            }
            field.setAccessible(true);
            try {
                return field.getBoolean(this);
            } catch (IllegalAccessException ignored) {
            }
        }
        return false;
    }

    @Inject(
            method = "handleLectern",
            at = @At("RETURN"),
            remap = false
    )
    private void mixinHandleLectern(ServerPlayer player, LecternControllerBlockEntity lectern, CallbackInfo ci) {
        LinkedControllerWireServerHandler.receivePressed(player.level(), lectern.getBlockPos(), findActivatedButtons(), findPressState());
    }

    @Inject(
            method = "handleItem",
            at = @At("RETURN"),
            remap = false
    )
    private void mixinHandleItem(ServerPlayer player, ItemStack heldItem, CallbackInfo ci) {
        HubItem.ifHubPresent(heldItem, pos -> LinkedControllerWireServerHandler.receivePressed(player.level(), pos, findActivatedButtons(), findPressState()));
    }
}
