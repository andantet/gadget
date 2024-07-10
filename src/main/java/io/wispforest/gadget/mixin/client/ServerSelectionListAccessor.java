package io.wispforest.gadget.mixin.client;

import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerSelectionList.class)
public interface ServerSelectionListAccessor {

    @Accessor
    static Component getCANT_RESOLVE_TEXT() {
        throw new AssertionError();
    }

    @Accessor
    static Component getCANT_CONNECT_TEXT() {
        throw new AssertionError();
    }

}
