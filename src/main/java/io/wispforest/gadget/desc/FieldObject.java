package io.wispforest.gadget.desc;

import io.wispforest.endec.annotations.SealedPolymorphic;

@SealedPolymorphic
public sealed interface FieldObject permits BytesFieldObject, ComplexFieldObject, ErrorFieldObject, NbtCompoundFieldObject, PrimitiveFieldObject {
    String type();

    int color();
}
