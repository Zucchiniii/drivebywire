package edn.stratodonut.drivebywire.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

public class HubItem {
    public static void putHub(ItemStack itemStack, BlockPos pos) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = customData.copyTag();
        nbt.putLong("Hub", pos.asLong());
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }
    
    public static void ifHubPresent(ItemStack itemStack, Consumer<BlockPos> runnable) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Hub", Tag.TAG_LONG)) {
                runnable.accept(BlockPos.of(tag.getLong("Hub")));
            }
        }
    }
}
