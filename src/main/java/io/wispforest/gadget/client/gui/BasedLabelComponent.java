package io.wispforest.gadget.client.gui;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Size;
import net.minecraft.network.chat.Component;

public class BasedLabelComponent extends LabelComponent {
    public BasedLabelComponent(Component text) {
        super(text);
    }

    @Override
    public void inflate(Size space) {
        int newMaxWidth = space.width();

        if (maxWidth != newMaxWidth)
            maxWidth = newMaxWidth;

        super.inflate(space);
    }
}
