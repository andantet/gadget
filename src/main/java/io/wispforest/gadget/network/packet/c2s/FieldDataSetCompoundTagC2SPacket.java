package io.wispforest.gadget.network.packet.c2s;

import io.wispforest.gadget.network.InspectionTarget;
import io.wispforest.gadget.path.ObjectPath;
import net.minecraft.nbt.CompoundTag;

public record FieldDataSetCompoundTagC2SPacket(InspectionTarget target, ObjectPath path, CompoundTag data) {
}
