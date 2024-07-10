package io.wispforest.gadget.client.nbt;

import io.wispforest.gadget.client.ServerData;
import io.wispforest.gadget.network.GadgetNetworking;
import io.wispforest.gadget.network.packet.c2s.ReplaceStackC2SPacket;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class StackNbtDataScreen extends BaseOwoScreen<FlowLayout> {
    private final NbtDataIsland island;
    private final AbstractContainerScreen<?> parent;

    public StackNbtDataScreen(AbstractContainerScreen<?> parent, Slot slot) {
        var stack = slot.getItem();
        Consumer<CompoundTag> reloader = null;

        if (ServerData.canReplaceStacks()) {
            reloader = newNbt -> {
                stack.setTag(newNbt);

                if (parent instanceof CreativeModeInventoryScreen) {
                    // Let it handle it.
                    return;
                }

                GadgetNetworking.CHANNEL.clientHandle().send(new ReplaceStackC2SPacket(slot.index, stack));
            };
        }

        CompoundTag tag = stack.getTag();

        if (tag == null) tag = new CompoundTag();

        this.parent = parent;
        this.island = new NbtDataIsland(tag, reloader);
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .verticalAlignment(VerticalAlignment.CENTER)
            .surface(Surface.VANILLA_TRANSLUCENT);


        FlowLayout main = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

        ScrollContainer<FlowLayout> scroll = Containers.verticalScroll(Sizing.fill(95), Sizing.fill(100), main)
            .scrollbar(ScrollContainer.Scrollbar.flat(Color.ofArgb(0xA0FFFFFF)));

        rootComponent.child(scroll.child(main));

        main
            .padding(Insets.of(15));

        main.child(island);

        FlowLayout sidebar = Containers.verticalFlow(Sizing.content(), Sizing.content());

        if (island.reloader != null) {
            var addButton = Containers.verticalFlow(Sizing.fixed(16), Sizing.fixed(16))
                .child(Components.label(Component.literal("+"))
                    .verticalTextAlignment(VerticalAlignment.CENTER)
                    .horizontalTextAlignment(HorizontalAlignment.CENTER)
                    .positioning(Positioning.absolute(5, 4))
                    .cursorStyle(CursorStyle.HAND)
                );

            addButton
                .cursorStyle(CursorStyle.HAND);

            addButton.mouseEnter().subscribe(
                () -> addButton.surface(Surface.flat(0x80ffffff)));

            addButton.mouseLeave().subscribe(
                () -> addButton.surface(Surface.BLANK));

            addButton.mouseDown().subscribe((mouseX, mouseY, button) -> {
                if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

                UISounds.playInteractionSound();

                island.typeSelector(
                    (int) (addButton.x() + mouseX),
                    (int) (addButton.y() + mouseY),
                    type -> island.child(new KeyAdderWidget(island, NbtPath.EMPTY, type, unused -> true)));

                return true;
            });

            sidebar.child(addButton);
        }

        sidebar
            .positioning(Positioning.absolute(0, 0))
            .padding(Insets.of(5));

        rootComponent.child(sidebar);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
