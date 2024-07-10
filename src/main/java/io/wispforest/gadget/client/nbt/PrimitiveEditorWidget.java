package io.wispforest.gadget.client.nbt;

import io.wispforest.gadget.client.gui.GuiUtil;
import io.wispforest.gadget.client.gui.TabTextBoxComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

public class PrimitiveEditorWidget extends FlowLayout {
    private final NbtDataIsland island;
    private final NbtPath path;
    private final Object value;
    private final Function<String, Tag> parser;

    private final LabelComponent contentsLabel;
    private final LabelComponent editLabel;
    private final TextBoxComponent editField;

    protected PrimitiveEditorWidget(NbtDataIsland island, NbtPath path, Object value, Function<String, Tag> parser) {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);
        this.island = island;
        this.path = path;

        this.contentsLabel = Components.label(
            Component.literal(value.toString())
                .withStyle(ChatFormatting.GRAY)
        );
        this.value = value;
        this.parser = parser;
        this.editLabel = Components.label(Component.literal(" ✎ "));
        this.editField = new TabTextBoxComponent(Sizing.fixed(100));

        GuiUtil.semiButton(this.editLabel, this::startEditing);
        this.editField.focusLost().subscribe(this::editFieldFocusLost);
        this.editField.keyPress().subscribe(this::editFieldKeyPressed);
        this.editField
            .verticalSizing(Sizing.fixed(8));

        child(contentsLabel);
        child(editLabel);
    }

    private boolean editFieldKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            UISounds.playButtonSound();

            path.set(island.data, parser.apply(editField.getValue()));

            island.reloadPath(path);
            island.reloader.accept(island.data);

            removeChild(editField);

            child(contentsLabel);
            child(editLabel);
            return true;
        }

        return false;
    }

    private void editFieldFocusLost() {
        removeChild(editField);

        child(contentsLabel);
        child(editLabel);
    }

    private void startEditing() {
        removeChild(contentsLabel);
        removeChild(editLabel);

        child(editField);
        editField.setValue(value.toString());
        editField.moveCursorToStart(false);

        if (focusHandler() != null)
            focusHandler().focus(editField, FocusSource.MOUSE_CLICK);

        editField.setFocused(true);
    }
}
