package io.wispforest.gadget.mixin.client;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSelectionList.Entry.class)
public interface AbstractSelectionListEntryMixin {
    @Accessor
    AbstractSelectionList<?> getList();
}
