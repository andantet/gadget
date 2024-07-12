package io.wispforest.gadget.desc.edit;

import io.wispforest.endec.Endec;

public interface PrimitiveEditType<T> {
    Endec<PrimitiveEditType<?>> ENDEC = PrimitiveEditTypes.ENDEC;

    T fromPacket(String repr);
    String toPacket(T value);
}