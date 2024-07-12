package io.wispforest.gadget.dump.read.unwrapped;

import org.jetbrains.annotations.Nullable;

public record CustomPayloadUnwrappedPacket(Object payload) implements FieldsUnwrappedPacket {
    @Override
    public @Nullable Object rawFieldsObject() {
        return payload;
    }
}
