package io.wispforest.gadget.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record BlockEntityTarget(BlockPos pos) implements InspectionTarget {
    @Override
    public @Nullable Object resolve(Level l) {
        return l.getBlockEntity(pos);
    }
}
