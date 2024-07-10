package io.wispforest.gadget.mixin;

import io.netty.channel.ChannelHandlerContext;
import io.wispforest.gadget.client.dump.ClientPacketDumper;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Shadow @Final private PacketFlow receiving;
    @Shadow private volatile PacketListener packetListener;
    @Unique private ConnectionProtocol readState;
    @Unique private ConnectionProtocol writeState;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void readHook(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (receiving == PacketFlow.SERVERBOUND) return;
        
        if (readState == null) {
            readState = packetListener.protocol();
        }

        ClientPacketDumper.dump(packet, readState, PacketFlow.CLIENTBOUND);
        
        ConnectionProtocol nextState = packet.nextProtocol();
        if (nextState != null) {
            readState = nextState;
        }
    }

    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void writeHook(Packet<?> packet, @Nullable PacketSendListener callbacks, boolean flush, CallbackInfo ci) {
        if (receiving == PacketFlow.SERVERBOUND) return;

        if (writeState == null) {
            writeState = packetListener.protocol();
        }

        if (packet instanceof ClientIntentionPacket) {
            writeState = ConnectionProtocol.HANDSHAKING;
        }
        
        ClientPacketDumper.dump(packet, writeState, PacketFlow.SERVERBOUND);

        ConnectionProtocol nextState = packet.nextProtocol();
        if (nextState != null) {
            writeState = nextState;
        }
    }
}
