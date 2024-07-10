package io.wispforest.gadget.mixin.client;

import io.wispforest.gadget.client.PoseStackLogger;
import io.wispforest.gadget.client.dump.ClientPacketDumper;
import io.wispforest.gadget.client.dump.DumpPrimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow private volatile boolean pause;

    @Inject(method = "runTick", at = @At("RETURN"))
    private void tripModelViewStack(boolean tick, CallbackInfo ci) {
        PoseStackLogger.startLoggingIfNeeded();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("RETURN"))
    private void onDisconnect(Screen screen, CallbackInfo ci) {
        if (DumpPrimer.isPrimed) {
            ClientPacketDumper.start(false);

            DumpPrimer.isPrimed = false;
        } else if (ClientPacketDumper.isDumping()) {
            ClientPacketDumper.stop();
        }
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/Minecraft;pause:Z", shift = At.Shift.AFTER))
    private void flushDump(boolean tick, CallbackInfo ci) {
        if (this.pause)
            ClientPacketDumper.flushIfNeeded();
    }
}
