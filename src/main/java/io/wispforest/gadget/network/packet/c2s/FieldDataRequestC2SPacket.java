package io.wispforest.gadget.network.packet.c2s;

import io.wispforest.gadget.network.InspectionTarget;
import io.wispforest.gadget.network.packet.s2c.FieldDataErrorS2CPacket;
import io.wispforest.gadget.path.ObjectPath;
import net.minecraft.network.chat.Component;

public record FieldDataRequestC2SPacket(InspectionTarget target, ObjectPath path, int from, int limit) {
    public FieldDataErrorS2CPacket replyWithError(Component message) {
        return new FieldDataErrorS2CPacket(target, path, message);
    }
}
