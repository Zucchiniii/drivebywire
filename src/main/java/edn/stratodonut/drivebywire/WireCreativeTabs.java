package edn.stratodonut.drivebywire;

import com.tterrag.registrate.util.entry.RegistryEntry;
import edn.stratodonut.drivebywire.blocks.TweakedControllerHubBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import static edn.stratodonut.drivebywire.DriveByWireMod.REGISTRATE;

public class WireCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DriveByWireMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE_CREATIVE_TAB = REGISTER.register("base",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.drivebywire"))
                .icon(() -> WireItems.WIRE.get().getDefaultInstance())
                    .displayItems((displayParams, output) -> {
                        for (RegistryEntry<Block> entry : REGISTRATE.getAll(Registries.BLOCK)) {
                            if (include(entry.get())) output.accept(entry.get().asItem());
                        }

                        for (RegistryEntry<Item> entry : REGISTRATE.getAll(Registries.ITEM)) {
                            if (include(entry.get())) output.accept(entry.get());
                        }
                    })
                    .build());

    public static boolean include(Object thing) {
        if (!ModList.get().isLoaded("create_tweaked_controllers")) {
            if (thing instanceof TweakedControllerHubBlock) return false;
        }
        return true;
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
