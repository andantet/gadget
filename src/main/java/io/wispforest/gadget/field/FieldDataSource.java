package io.wispforest.gadget.field;

import io.wispforest.gadget.desc.edit.PrimitiveEditData;
import io.wispforest.gadget.network.FieldData;
import io.wispforest.gadget.path.ObjectPath;
import io.wispforest.gadget.path.PathStep;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface FieldDataSource {
    FieldData rootData();

    Map<PathStep, FieldData> initialRootFields();

    CompletableFuture<Map<PathStep, FieldData>> requestFieldsOf(ObjectPath path, int from, int limit);

    default boolean isMutable() {
        return false;
    }

    default CompletableFuture<Void> setPrimitiveAt(ObjectPath path, PrimitiveEditData editData) {
        throw new UnsupportedOperationException();
    }

    default CompletableFuture<Void> setCompoundTagAt(ObjectPath path, CompoundTag tag) {
        throw new UnsupportedOperationException();
    }
}
