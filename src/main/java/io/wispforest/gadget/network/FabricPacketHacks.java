package io.wispforest.gadget.network;

import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class FabricPacketHacks {
    private static final Map<ResourceLocation, PacketType<?>> TYPES = new HashMap<>();

    public static PacketType<?> getForId(ResourceLocation id) {
        return TYPES.get(id);
    }

    @ApiStatus.Internal
    public static void saveType(PacketType<?> type) {
        TYPES.put(type.getId(), type);
    }
}
