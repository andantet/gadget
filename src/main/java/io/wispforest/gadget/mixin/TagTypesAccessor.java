package io.wispforest.gadget.mixin;

import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagTypes.class)
public interface TagTypesAccessor {
    @Accessor
    static TagType<?>[] getTYPES() {
        throw new UnsupportedOperationException();
    }
}
