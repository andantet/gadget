package io.wispforest.gadget.network.packet.c2s;

import net.minecraft.resources.ResourceLocation;

public record RequestResourceC2SPacket(ResourceLocation id, int index) {
}
