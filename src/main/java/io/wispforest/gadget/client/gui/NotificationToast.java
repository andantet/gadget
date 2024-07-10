package io.wispforest.gadget.client.gui;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public class NotificationToast implements Toast {
    private final OwoUIAdapter<FlowLayout> adapter;
    private final Minecraft client = Minecraft.getInstance();

    public NotificationToast(Component headText, Component messageText) {
        this.adapter = OwoUIAdapter.createWithoutScreen(0, 0, 160, 32, Containers::verticalFlow);

        var root = this.adapter.rootComponent;

        root
            .child(Components.label(headText)
                .maxWidth(160)
                .horizontalTextAlignment(HorizontalAlignment.CENTER))
            .surface(Surface.VANILLA_TRANSLUCENT.and(Surface.outline(0xFF5800FF)))
            .allowOverflow(true)
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.CENTER)
            .padding(Insets.of(5));

        if (messageText != null)
            root.child(Components.label(messageText));

        this.adapter.inflateAndMount();
    }

    public void register() {
        if (!client.isSameThread()) {
            client.execute(this::register);
            return;
        }

        client.getToasts().addToast(this);
    }

    @Override
    public Visibility render(GuiGraphics ctx, ToastComponent manager, long startTime) {
        this.adapter.render(ctx, 0, 0, client.getFrameTime());

        return startTime > 5000 ? Visibility.HIDE : Visibility.SHOW;
    }
}
