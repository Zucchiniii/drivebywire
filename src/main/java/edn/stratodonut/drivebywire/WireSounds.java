package edn.stratodonut.drivebywire;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WireSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, DriveByWireMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PLUG_IN = registerSoundEvents("plug_in");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLUG_OUT = registerSoundEvents("plug_out");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DriveByWireMod.MOD_ID, name)));
    }

    public static void register(IEventBus bus) { SOUND_EVENTS.register(bus); }
}
