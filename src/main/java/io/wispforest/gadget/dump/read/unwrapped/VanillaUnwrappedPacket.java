package io.wispforest.gadget.dump.read.unwrapped;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.Nullable;

public record VanillaUnwrappedPacket(Packet<?> packet) implements FieldsUnwrappedPacket {
    @Override
    public @Nullable Component headText() {
        return null;
    }

    @Override
    public @Nullable Object rawFieldsObject() {
        return packet;
    }
}
