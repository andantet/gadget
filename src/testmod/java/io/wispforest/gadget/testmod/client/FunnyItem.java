package io.wispforest.gadget.testmod.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FunnyItem extends Item {
    public FunnyItem() {
        super(new Item.Properties());
    }

    @Override
    public Component getName(ItemStack stack) {
        if (Screen.hasShiftDown()) {
            stack.getOrCreateTag().putString("owl", "yay");
        }

        return super.getName(stack);
    }
}
