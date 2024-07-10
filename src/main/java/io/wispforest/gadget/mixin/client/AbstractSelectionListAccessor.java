package io.wispforest.gadget.mixin.client;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor extends GuiEventListener {
    @Invoker
    int callGetRowTop(int index);

    @Accessor
    int getItemHeight();
}
