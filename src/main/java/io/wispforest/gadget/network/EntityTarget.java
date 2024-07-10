package io.wispforest.gadget.network;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record EntityTarget(int networkId) implements InspectionTarget {
    @Override
    public @Nullable Object resolve(Level l) {
        return l.getEntity(networkId);
    }
}
