package io.wispforest.gadget.network;

import io.wispforest.endec.annotations.SealedPolymorphic;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SealedPolymorphic
public sealed interface InspectionTarget permits BlockEntityTarget, EntityTarget {
    @Nullable Object resolve(World w);
}
