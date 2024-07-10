package io.wispforest.gadget.client.gui;

import io.wispforest.owo.ui.component.DropdownComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.ui.layers.Layers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;

public class ContextMenuScreens {
    private static final Layer<Screen, FlowLayout> LAYER = Layers.add(
        Containers::verticalFlow,
        instance -> { },
        SelectWorldScreen.class, JoinMultiplayerScreen.class
    );

    public static void init() {

    }

    public static DropdownComponent contextMenuAt(Screen screen, double mouseX, double mouseY) {
        return GuiUtil.contextMenu(LAYER.getInstance(screen).adapter.rootComponent, mouseX, mouseY);
    }
}
