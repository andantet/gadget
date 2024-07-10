package io.wispforest.gadget.desc;

import net.minecraft.nbt.CompoundTag;

public record CompoundTagFieldObject(CompoundTag data) implements FieldObject {
    @Override
    public String type() {
        return "nbt";
    }

    @Override
    public int color() {
        return 0xFF0000;
    }
}
