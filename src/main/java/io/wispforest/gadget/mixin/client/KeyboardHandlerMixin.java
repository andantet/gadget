package io.wispforest.gadget.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.wispforest.gadget.Gadget;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract boolean handleDebugKeys(int key);

    @Inject(method = "method_1454(ILnet/minecraft/client/gui/screens/Screen;[ZIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(III)Z", shift = At.Shift.BY, by = 2))
    private static void afterKeyPressed(int code, Screen screen, boolean[] resultHack, int key, int scancode, int modifiers, CallbackInfo ci) {
        var client = Minecraft.getInstance();

        if (resultHack[0]) return;
        if (!Gadget.CONFIG.debugKeysInScreens()) return;
        if (!InputConstants.isKeyDown(client.getWindow().getWindow(), GLFW.GLFW_KEY_F3)) return;

        resultHack[0] = ((KeyboardHandlerMixin)(Object) client.keyboardHandler).handleDebugKeys(key);
    }

    @Inject(method = "handleDebugKeys", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/player/LocalPlayer;"), cancellable = true)
    private void leaveIfPlayer(int key, CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.player == null)
            cir.setReturnValue(false);
    }

    @Inject(method = "handleDebugKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasPermissions(I)Z"), cancellable = true)
    private void leaveOnGameModeSelection(int key, CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.player == null)
            cir.setReturnValue(false);
    }
}
