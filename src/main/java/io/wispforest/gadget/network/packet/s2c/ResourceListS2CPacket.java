package io.wispforest.gadget.network.packet.s2c;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record ResourceListS2CPacket(Map<ResourceLocation, Integer> resources) {
}
