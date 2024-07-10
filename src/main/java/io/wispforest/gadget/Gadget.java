package io.wispforest.gadget;

import io.wispforest.gadget.dump.read.handler.PacketHandlers;
import io.wispforest.gadget.mappings.MappingsManager;
import io.wispforest.gadget.nbt.NbtLocks;
import io.wispforest.gadget.network.GadgetNetworking;
import io.wispforest.gadget.util.GadgetConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gadget implements ModInitializer {
    public static final String MODID = "gadget";
    public static final GadgetConfig CONFIG = GadgetConfig.createAndLoad();
    public static final Logger LOGGER = LoggerFactory.getLogger("gadget");

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    @Override
    public void onInitialize() {
        GadgetNetworking.init();
        PacketHandlers.init();
        NbtLocks.init();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
            MappingsManager.init();

        for (EntrypointContainer<GadgetEntrypoint> container : FabricLoader.getInstance().getEntrypointContainers("gadget:init", GadgetEntrypoint.class)) {
            try {
                container.getEntrypoint().onGadgetInit();
            } catch (Exception e) {
                LOGGER.error("{}'s `gadget:init` entrypoint handler threw an exception",
                    container.getProvider().getMetadata().getId(), e);
            }
        }
    }
}
