package io.wispforest.gadget.client.field;

import io.wispforest.gadget.client.gui.SubObjectContainer;
import io.wispforest.gadget.client.nbt.NbtDataIsland;
import io.wispforest.gadget.desc.*;
import io.wispforest.gadget.desc.edit.PrimitiveEditData;
import io.wispforest.gadget.network.GadgetNetworking;
import io.wispforest.gadget.network.packet.c2s.SetNbtCompoundC2SPacket;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.gadget.network.FieldData;
import io.wispforest.gadget.path.ObjectPath;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FieldDataIsland {
    protected final Map<ObjectPath, ClientFieldData> fields = new TreeMap<>();
    private final VerticalFlowLayout mainContainer;
    private Consumer<ObjectPath> pathRequester = path -> {};
    private boolean shortenNames = false;
    BiConsumer<ObjectPath, PrimitiveEditData> primitiveSetter = null;
    BiConsumer<ObjectPath, NbtCompound> nbtCompoundSetter = null;

    public FieldDataIsland() {
        this.mainContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());
    }

    public void pathRequester(Consumer<ObjectPath> pathRequester) {
        this.pathRequester = pathRequester;
    }

    public void primitiveSetter(BiConsumer<ObjectPath, PrimitiveEditData> primitiveSetter) {
        this.primitiveSetter = primitiveSetter;
    }

    public void nbtCompoundSetter(BiConsumer<ObjectPath, NbtCompound> nbtCompoundSetter) {
        this.nbtCompoundSetter = nbtCompoundSetter;
    }

    public void targetObject(Object obj, boolean settable) {
        this.pathRequester = (path) -> {
            Object sub = path.follow(obj);

            FieldObjects.collectAllData(path, sub)
                .forEach(this::addFieldData);
        };

        if (settable) {
            this.primitiveSetter = (path, data) -> {
                path.set(obj, data.toObject());

                var parentPath = path.parent();

                FieldObjects.collectAllData(parentPath, parentPath.follow(obj))
                    .forEach(this::addFieldData);
            };

            this.nbtCompoundSetter = (path, data) -> {
                path.set(obj, data);

                var parentPath = path.parent();

                FieldObjects.collectAllData(parentPath, parentPath.follow(obj))
                    .forEach(this::addFieldData);
            };
        }

        FieldObjects.collectAllData(ObjectPath.EMPTY, obj)
            .forEach(this::addFieldData);
    }

    public void shortenNames() {
        this.shortenNames = true;
    }

    public VerticalFlowLayout mainContainer() {
        return mainContainer;
    }

    private void makeComponent(VerticalFlowLayout container, ObjectPath path, ClientFieldData data) {
        var rowContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());
        var row = Containers.horizontalFlow(Sizing.content(), Sizing.content());

        data.containerComponent = rowContainer;

        rowContainer.child(row);

        var nameText = Text.literal(path.name());

        if (data.isMixin)
            nameText.formatted(Formatting.GRAY)
                .styled(x -> x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.literal("Mixin-injected field")
                        .formatted(Formatting.YELLOW))));

        MutableText rowText = Text.literal("")
            .append(Text.literal(data.obj.type().charAt(0) + " ")
                .styled(x -> x.withColor(data.obj.color())))
            .append(nameText);
        var rowLabel = Components.label(rowText);

        row.child(rowLabel);

        if (data.obj instanceof PrimitiveFieldObject pfo) {
            if (!data.isFinal && primitiveSetter != null && pfo.editData().isPresent()) {
                rowText.append(Text.literal(" = ")
                    .formatted(Formatting.GRAY));
                row.child(new PrimitiveFieldWidget(this, path, pfo));
            } else {
                rowText.append(Text.literal(" = " + pfo.contents())
                    .formatted(Formatting.GRAY));
            }
        } else if (data.obj instanceof ErrorFieldObject efo) {
            rowText.append(Text.literal(" " + efo.exceptionClass())
                .styled(x -> x
                    .withColor(Formatting.RED)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(efo.fullExceptionText())))));
        } else if (data.obj instanceof ComplexFieldObject cfo) {
            var subContainer = new SubObjectContainer(
                () -> pathRequester.accept(path),
                () -> { });
            data.subObjectContainer = subContainer;
            rowContainer.child(subContainer);

            String text = cfo.text();

            if (shortenNames)
                text = text.substring(text.lastIndexOf('.') + 1);

            rowText.append(
                Text.literal(" " + text + " ")
                    .formatted(Formatting.GRAY)
            );

            row
                .child(subContainer.getSpinnyBoi()
                    .sizing(Sizing.fixed(10), Sizing.content()));
        } else if (data.obj instanceof NbtCompoundFieldObject nfo) {
            var subContainer = new SubObjectContainer(
                () -> { },
                () -> { });
            data.subObjectContainer = subContainer;
            rowContainer.child(subContainer);

            Consumer<NbtCompound> reloader = null;

            if (nbtCompoundSetter != null)
                reloader = newData -> nbtCompoundSetter.accept(path, newData);

            subContainer.child(new NbtDataIsland(nfo.data(), reloader));
            row
                .child(subContainer.getSpinnyBoi()
                    .sizing(Sizing.fixed(10), Sizing.content()));
        }

        row
            .margins(Insets.both(0, 2))
            .allowOverflow(true);

        container.child(rowContainer);
    }

    public void addFieldData(ObjectPath path, FieldData data) {
        if (mainContainer == null) {
            fields.put(path, new ClientFieldData(data));
            return;
        }

        ClientFieldData old = fields.get(path);
        VerticalFlowLayout container;

        if (path.steps().length == 1) {
            container = mainContainer;
        } else {
            container = fields.get(path.parent()).subObjectContainer;
        }

        if (old != null) {
            container.removeChild(old.containerComponent);
        }

        ClientFieldData newData = new ClientFieldData(data);

        makeComponent(container, path, newData);

        fields.put(path, newData);
    }
}
