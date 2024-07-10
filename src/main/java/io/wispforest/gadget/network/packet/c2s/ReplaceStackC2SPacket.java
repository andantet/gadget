package io.wispforest.gadget.network.packet.c2s;

import net.minecraft.world.item.ItemStack;

public record ReplaceStackC2SPacket(int slotId, ItemStack stack) {
}
