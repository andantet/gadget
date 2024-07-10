package io.wispforest.gadget.mixin.client;

import io.wispforest.gadget.client.dump.ClientPacketDumper;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void disableDump(Screen parent, Component title, Component reason, CallbackInfo ci) {
        if (ClientPacketDumper.isDumping())
            ClientPacketDumper.stop();
    }
}
