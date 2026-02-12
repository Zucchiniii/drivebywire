package edn.stratodonut.drivebywire;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import edn.stratodonut.drivebywire.wire.ShipWireNetworkManager;
import edn.stratodonut.drivebywire.client.ClientWireNetworkHandler;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("drivebywire")
public class DriveByWireMod
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "drivebywire";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));
    }

    public DriveByWireMod(IEventBus modEventBus) { onCtor(modEventBus); }

    public void onCtor(IEventBus modEventBus) {
        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(this::onCommonSetup);

        ValkyrienSkies.api().registerAttachment(ValkyrienSkies.api().newAttachmentRegistrationBuilder(ShipWireNetworkManager.class)
                .useLegacySerializer()
                .build()
        );

        // TODO: CHANGE LOGO
        // TODO: Test with audience(?)
        WireCreativeTabs.register(modEventBus);
        WireBlocks.register();
        WireBlockEntities.register();
        WireItems.register();
        modEventBus.addListener(WirePackets::registerPayloads);

        WireSounds.register(modEventBus);
        NeoForge.EVENT_BUS.register(ServerEvents.class);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(ClientWireNetworkHandler.class);
            onCtorClient();
        }
    }

    private void onCtorClient() {
        PonderIndex.addPlugin(new WirePonderPlugin());
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // DO NOTHING
    }

    public static void warn(String format, Object arg) {
        LOGGER.warn(format, arg);
    }

    public static void warn(String format, Object... args) {
        LOGGER.warn(format, args);
    }

    public static ResourceLocation getResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
