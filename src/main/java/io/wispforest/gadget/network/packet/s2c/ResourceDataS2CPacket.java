package io.wispforest.gadget.network.packet.s2c;

import net.minecraft.resources.ResourceLocation;

public record ResourceDataS2CPacket(ResourceLocation id, byte[] data) {
}
