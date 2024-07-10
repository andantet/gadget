package io.wispforest.gadget.client.nbt;

import io.wispforest.gadget.client.gui.GuiUtil;
import io.wispforest.gadget.client.gui.SubObjectContainer;
import io.wispforest.gadget.mixin.TagTypesAccessor;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class NbtDataIsland extends FlowLayout {
    private final Map<NbtPath, WidgetData> elements = new HashMap<>();

    final CompoundTag data;
    final @Nullable Consumer<CompoundTag> reloader;

    public NbtDataIsland(CompoundTag data, @Nullable Consumer<CompoundTag> reloader) {
        super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

        this.data = data;
        this.reloader = reloader;

        for (String key : data.getAllKeys()) {
            makeComponent(new NbtPath(new String[] {key}), data.get(key));
        }
    }

    void makeComponent(NbtPath path, Tag element) {
        FlowLayout full = Containers.verticalFlow(Sizing.content(), Sizing.content());
        FlowLayout row = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        full.child(row);

        var parentContainer = subContainerOf(path.parent());
        WidgetData widgetData = new WidgetData();
        widgetData.fullContainer = full;

        WidgetData old = elements.get(path);
        int idx = parentContainer.children().size();

        if (parentContainer instanceof SubObjectContainer subObj)
            idx = subObj.collapsibleChildren().size();

        if (old != null) {
            int newIdx = parentContainer.children().indexOf(old.fullContainer);

            if (newIdx != -1)
                idx = newIdx;

            parentContainer.removeChild(old.fullContainer);
            elements.entrySet().removeIf(entry -> entry.getKey().startsWith(path));
        }

        elements.put(path, widgetData);

        MutableComponent rowText = Component.literal("");
        LabelComponent label = Components.label(rowText);

        row.child(label);

        rowText.append(typeText(element.getType(), ""));
        rowText.append(" " + path.name() + " ");

        if (element instanceof StringTag string) {
            rowText.append(Component.literal("= ")
                .withStyle(ChatFormatting.GRAY));

            if (reloader != null) {
                row.child(new PrimitiveEditorWidget(this, path, string.getAsString(), StringTag::valueOf));
            } else {
                rowText.append(Component.literal( string.getAsString() + " ")
                    .withStyle(ChatFormatting.GRAY));
            }
        } else if (element instanceof NumericTag number) {
            rowText.append(Component.literal("= ")
                .withStyle(ChatFormatting.GRAY));

            if (reloader != null) {
                row.child(new PrimitiveEditorWidget(this, path, number.getAsNumber(), text -> {
                    if (number instanceof ByteTag)
                        return ByteTag.valueOf(Byte.parseByte(text));
                    else if (number instanceof ShortTag)
                        return ShortTag.valueOf(Short.parseShort(text));
                    else if (number instanceof IntTag)
                        return IntTag.valueOf(Integer.parseInt(text));
                    else if (number instanceof LongTag)
                        return LongTag.valueOf(Long.parseLong(text));
                    else if (number instanceof FloatTag)
                        return FloatTag.valueOf(Float.parseFloat(text));
                    else if (number instanceof DoubleTag)
                        return DoubleTag.valueOf(Double.parseDouble(text));
                    else
                        throw new IllegalStateException("Unknown AbstractNbtNumber type!");
                }));
            } else {
                rowText.append(Component.literal( number.getAsNumber() + " ")
                    .withStyle(ChatFormatting.GRAY));
            }
        } else if (element instanceof CompoundTag compound) {
            widgetData.subContainer = new SubObjectContainer(unused -> {}, unused -> {});

            row.child(widgetData.subContainer.getSpinnyBoi());

            full.child(widgetData.subContainer);

            for (String key : compound.getAllKeys()) {
                Tag sub = compound.get(key);
                var subPath = path.then(key);

                makeComponent(subPath, sub);
            }

            if (reloader != null) {
                var plusLabel = Components.label(Component.nullToEmpty("+ "));

                GuiUtil.semiButton(plusLabel, (mouseX, mouseY) ->
                    typeSelector(
                        (int) (plusLabel.x() + mouseX),
                        (int) (plusLabel.y() + mouseY),
                        type -> widgetData.subContainer.child(new KeyAdderWidget(this, path, type, unused -> true)))
                );

                row.child(plusLabel);
            }
        } else if (element instanceof CollectionTag<?> list) {
            widgetData.subContainer = new SubObjectContainer(unused -> {}, unused -> {});

            row.child(widgetData.subContainer.getSpinnyBoi());

            full.child(widgetData.subContainer);

            for (int i = 0; i < list.size(); i++) {
                Tag sub = list.get(i);
                var subPath = path.then(String.valueOf(i));

                makeComponent(subPath, sub);
            }

            if (reloader != null) {
                var plusLabel = Components.label(Component.nullToEmpty("+ "));
                Predicate<String> nameVerifier = name -> {
                    try {
                        var index = Integer.parseInt(name);

                        return index <= list.size();
                    } catch (NumberFormatException nfe) {
                        return false;
                    }
                };

                GuiUtil.semiButton(plusLabel, (mouseX, mouseY) -> {
                    if (list instanceof ListTag) {
                        if (list.isEmpty()) {
                            typeSelector(
                                (int) (plusLabel.x() + mouseX),
                                (int) (plusLabel.y() + mouseY),
                                type -> widgetData.subContainer.child(new KeyAdderWidget(this, path, type, nameVerifier)));
                        } else {
                            widgetData.subContainer.child(
                                new KeyAdderWidget(this, path, TagTypes.getType(list.getElementType()), nameVerifier));
                        }
                    } else if (list instanceof ByteArrayTag) {
                        widgetData.subContainer.child(
                            new KeyAdderWidget(this, path, ByteTag.TYPE, nameVerifier));
                    } else if (list instanceof IntArrayTag) {
                        widgetData.subContainer.child(
                            new KeyAdderWidget(this, path, IntTag.TYPE, nameVerifier));
                    } else if (list instanceof LongArrayTag) {
                        widgetData.subContainer.child(
                            new KeyAdderWidget(this, path, LongTag.TYPE, nameVerifier));
                    }
                });

                row.child(plusLabel);
            }
        }

        FlowLayout target = subContainerOf(path.parent());

        if (reloader != null) {
            var crossLabel = Components.label(Component.literal("❌"));
            GuiUtil.semiButton(crossLabel, () -> {
                path.remove(data);
                reloader.accept(data);
                target.removeChild(full);
                elements.entrySet().removeIf(entry -> entry.getKey().startsWith(path));
            });
            crossLabel.margins(Insets.right(5));
            row.child(crossLabel);
        }

        var copyLabel = Components.label(Component.literal("C"));
        copyLabel.tooltip(Component.translatable("chat.copy.click"));
        GuiUtil.semiButton(copyLabel, () -> {
             Minecraft.getInstance().keyboardHandler.setClipboard(path.follow(data).getAsString());
        });
        row.child(copyLabel);

        row
            .margins(Insets.both(0, 2))
            .allowOverflow(true);

        target.child(idx, full);
    }

    public void typeSelector(int mouseX, int mouseY, Consumer<TagType<?>> consumer) {
        if (this.reloader == null) {
            throw new IllegalStateException("Tried to open type selector with read-only NBT island!");
        }

        var dropdown = GuiUtil.contextMenu(GuiUtil.root(this), mouseX, mouseY);

        for (TagType<?> type : TagTypesAccessor.getTYPES()) {
            dropdown.button(typeText(type, ".full"),
                unused -> consumer.accept(type));
        }
    }

    MutableComponent typeText(TagType<?> type, String suffix) {
        String name;

        if (type == CompoundTag.TYPE)
            name = "compound";
        else if (type == ListTag.TYPE)
            name = "list";
        else if (type == ByteArrayTag.TYPE)
            name = "byte_array";
        else if (type == IntArrayTag.TYPE)
            name = "int_array";
        else if (type == LongArrayTag.TYPE)
            name = "long_array";
        else if (type == StringTag.TYPE)
            name = "string";
        else if (type == ByteTag.TYPE)
            name = "byte";
        else if (type == ShortTag.TYPE)
            name = "short";
        else if (type == IntTag.TYPE)
            name = "int";
        else if (type == LongTag.TYPE)
            name = "long";
        else if (type == FloatTag.TYPE)
            name = "float";
        else if (type == DoubleTag.TYPE)
            name = "double";
        else if (type == EndTag.TYPE)
            name = "end";
        else
            name = "unknown";

        return Component.translatable("text.gadget.nbt." + name + suffix);
    }

    public void reloadPath(NbtPath path) {
        if (path.steps().length == 0) {
            clearChildren();

            for (String key : data.getAllKeys()) {
                makeComponent(new NbtPath(new String[] {key}), data.get(key));
            }
        } else {
            makeComponent(path, path.follow(data));
        }
    }

    private FlowLayout subContainerOf(NbtPath path) {
        if (path.steps().length == 0)
            return this;
        else
            return elements.get(path).subContainer;
    }

    private static class WidgetData {
        private FlowLayout fullContainer;
        private SubObjectContainer subContainer;

    }

}
