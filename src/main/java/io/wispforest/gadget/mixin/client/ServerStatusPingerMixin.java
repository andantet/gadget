package io.wispforest.gadget.mixin.client;

import io.wispforest.gadget.client.dump.ClientPacketDumper;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerStatusPinger.class)
public class ServerStatusPingerMixin {

    @Inject(method = "removeAll", at = @At(value = "HEAD"))
    private void stopDumpingOnCancel(CallbackInfo ci) {
        if (ClientPacketDumper.isDumping()) {
            ClientPacketDumper.stop();
        }
    }

    @Mixin(targets = "net/minecraft/client/multiplayer/ServerStatusPinger$1")
    public static class ClientStatusPacketListenerImplMixin {

        @Inject(method = "handlePongResponse", at = @At("HEAD"))
        private void stopDumpingOnPingResult(CallbackInfo ci) {
            if (ClientPacketDumper.isDumping()) {
                ClientPacketDumper.stop();
            }
        }

        @Inject(method = "onDisconnect", at = @At("HEAD"))
        private void stopDumpingOnDisconnected(CallbackInfo ci) {
            if (ClientPacketDumper.isDumping()) {
                ClientPacketDumper.stop();
            }
        }

    }

}
