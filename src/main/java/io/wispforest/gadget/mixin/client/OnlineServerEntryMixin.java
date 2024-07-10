package io.wispforest.gadget.mixin.client;

import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.client.dump.ClientPacketDumper;
import io.wispforest.gadget.client.dump.DumpPrimer;
import io.wispforest.gadget.client.gui.ContextMenuScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.UnknownHostException;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class OnlineServerEntryMixin {
    @Shadow @Final private JoinMultiplayerScreen screen;

    @Shadow @Final private ServerData serverData;

    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract void updateServerList();

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onRightClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;
        if (!Gadget.CONFIG.rightClickDump()) return;

        ContextMenuScreens.contextMenuAt(screen, mouseX, mouseY)
            .button(Component.translatable("text.gadget.join_with_dump"), dropdown2 -> {
                DumpPrimer.isPrimed = true;

                this.screen.setSelected((ServerSelectionList.OnlineServerEntry)(Object) this);
                this.screen.joinSelectedServer();
            })
            .button(Component.translatable("text.gadget.query_with_dump"), dropdown2 -> {
                ClientPacketDumper.start(false);

                try {
                    this.screen.getPinger().pingServer(this.serverData, () -> this.minecraft.execute(this::updateServerList));
                } catch (UnknownHostException var2x) {
                    this.serverData.ping = -1L;
                    this.serverData.motd = ServerSelectionListAccessor.getCANT_RESOLVE_TEXT();
                } catch (Exception var3x) {
                    this.serverData.ping = -1L;
                    this.serverData.motd = ServerSelectionListAccessor.getCANT_CONNECT_TEXT();
                }
            });

        cir.setReturnValue(true);
    }
}