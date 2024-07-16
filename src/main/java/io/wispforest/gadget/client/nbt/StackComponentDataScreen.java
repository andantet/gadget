package io.wispforest.gadget.client.nbt;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class StackComponentDataScreen extends BaseOwoScreen<FlowLayout> {
    private final ItemStack stack;
    private final HandledScreen<?> parent;

    public StackComponentDataScreen(HandledScreen<?> parent, Slot slot) {
        this.stack = slot.getStack();
        this.parent = parent;
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

        for (var component : stack.getComponents()) {
            FlowLayout full = Containers.verticalFlow(Sizing.content(), Sizing.content());
            FlowLayout row = Containers.horizontalFlow(Sizing.content(), Sizing.content());

            full.child(row);
            main.child(full);

            row.child(Components.label(Text.literal(Registries.DATA_COMPONENT_TYPE.getId(component.type()).toString())
                .append(Text.literal(" = ")
                    .formatted(Formatting.GRAY))));

            NbtElement tag = component.encode(client.world.getRegistryManager().getOps(NbtOps.INSTANCE)).getOrThrow();

            NbtCompound compound;
            if (tag instanceof NbtCompound c) {
                compound = c;
            } else {
                compound = new NbtCompound();
                compound.put("<value>", tag);
            }

            full.child(new NbtDataIsland(compound, null));
        }
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
