package io.wispforest.gadget.path;

import io.wispforest.endec.annotations.SealedPolymorphic;

@SealedPolymorphic
public sealed interface MapPathStepType permits EnumMapPathStepType, SimpleMapPathStepType {
    static MapPathStepType getFor(Class<?> klass) {
        if (klass.isEnum())
            return new EnumMapPathStepType(klass);
        else
            return SimpleMapPathStepType.getFor(klass);
    }

    Object fromNetwork(String data);
    String toNetwork(Object obj);
    String toPretty(Object obj);
}
