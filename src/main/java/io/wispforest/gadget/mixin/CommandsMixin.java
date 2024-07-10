package io.wispforest.gadget.mixin;

import io.wispforest.gadget.network.GadgetNetworking;
import io.wispforest.gadget.network.packet.s2c.AnnounceS2CPacket;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {
    @Inject(method = "sendCommands", at = @At("HEAD"))
    private void onReloadPermissions(ServerPlayer player, CallbackInfo ci) {
        boolean canReplaceStacks = Permissions.check(player, "gadget.replaceStack", 4);
        boolean canRequestServerData = Permissions.check(player, "gadget.requestServerData", 4);

        GadgetNetworking.CHANNEL.serverHandle(player).send(new AnnounceS2CPacket(canReplaceStacks, canRequestServerData));
    }
}
