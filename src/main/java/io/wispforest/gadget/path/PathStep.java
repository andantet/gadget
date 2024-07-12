package io.wispforest.gadget.path;

import io.wispforest.endec.annotations.SealedPolymorphic;

@SealedPolymorphic
public sealed interface PathStep permits FieldPathStep, IndexPathStep, MapPathStep {
    Object follow(Object o);

    void set(Object o, Object to);
}
