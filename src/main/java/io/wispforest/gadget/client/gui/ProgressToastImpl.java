package io.wispforest.gadget.client.gui;

import io.wispforest.gadget.util.ProgressToast;
import io.wispforest.owo.ui.component.BoxComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

import java.util.function.LongSupplier;

public class ProgressToastImpl implements Toast, ProgressToast {
    private OwoUIAdapter<FlowLayout> adapter;
    private final Minecraft client = Minecraft.getInstance();
    private boolean attached = false;

    private LabelComponent stepLabel;
    private BoxComponent progressBox;
    private long stopTime = 0;
    private LongSupplier following = null;
    private long followingTotal = 0;

    public ProgressToastImpl(Component headText) {
        this.adapter = OwoUIAdapter.createWithoutScreen(0, 0, 160, 32, Containers::verticalFlow);

        var root = this.adapter.rootComponent;

        root
            .child(Components.label(headText)
                .maxWidth(160)
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .margins(Insets.bottom(0)))
            .child(stepLabel = Components.label(Component.empty())
                .maxWidth(160)
                .horizontalTextAlignment(HorizontalAlignment.CENTER))
            .child((progressBox = Components.box(Sizing.fixed(0), Sizing.fixed(3)))
                .color(Color.WHITE)
                .fill(true)
                .positioning(Positioning.absolute(0, 15)))
            .surface(Surface.VANILLA_TRANSLUCENT.and(Surface.outline(0xFF5800FF)))
            .allowOverflow(true)
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.CENTER)
            .padding(Insets.of(10));

        this.adapter.inflateAndMount();
    }

    @Override
    public Visibility render(GuiGraphics ctx, ToastComponent manager, long startTime) {
        long value = following == null ? -1 : following.getAsLong();

        if (value < 0) {
            progressBox.horizontalSizing(Sizing.fixed(0));
            following = null;
        } else {
            progressBox.horizontalSizing(Sizing.fixed((int) (value * 140 / followingTotal)));
        }

        this.adapter.render(ctx, 0, 0, client.getFrameTime());

        if (stopTime == -1)
            stopTime = startTime + 1;
        else if (stopTime == -2)
            return Visibility.HIDE;

        if (stopTime == 0) {
            return Visibility.SHOW;
        } else {
            return startTime - stopTime > 2500 ? Visibility.HIDE : Visibility.SHOW;
        }
    }

    @Override
    public void step(Component text) {
        Minecraft.getInstance().execute(() -> {
            if (!attached) {
                Minecraft.getInstance().getToasts().addToast(this);
                attached = true;
            }

            this.stepLabel.text(text);
            this.following = null;
        });

    }

    @Override
    public void followProgress(LongSupplier following, long total) {
        Minecraft.getInstance().execute(() -> {
            this.following = following;
            this.followingTotal = total;
        });
    }

    @Override
    public void force() {
        Minecraft.getInstance().execute(() -> {
            if (!attached) {
                Minecraft.getInstance().getToasts().addToast(this);
                attached = true;
            }
        });
    }

    @Override
    public void finish(Component text, boolean hideImmediately) {
        Minecraft.getInstance().execute(() -> {
            this.stepLabel.text(text);
            this.following = null;
            stopTime = hideImmediately ? -2 : -1;
        });
    }

    public void oom(OutOfMemoryError oom) {
        adapter.rootComponent.clearChildren();
        client.screen.removed();
        client.screen = null;

        following = null;
        adapter = null;
        stepLabel = null;
        progressBox = null;

        client.execute(() -> {
            client.getToasts().clear();

            throw oom;
        });
    }
}
