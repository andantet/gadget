package io.wispforest.gadget.dump.read;

import io.wispforest.gadget.util.ContextData;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public final class DumpedPacket extends ContextData<DumpedPacket> {
    private final boolean outbound;
    private final ConnectionProtocol state;
    private final Packet<?> packet;
    private final ResourceLocation channelId;
    private final FriendlyByteBuf wrappedBuf;
    private final long sentAt;
    private final int size;

    public DumpedPacket(boolean outbound, ConnectionProtocol state, Packet<?> packet, ResourceLocation channelId, FriendlyByteBuf wrappedBuf,
                        long sentAt, int size) {
        this.outbound = outbound;
        this.state = state;
        this.packet = packet;
        this.channelId = channelId;
        this.wrappedBuf = wrappedBuf;
        this.sentAt = sentAt;
        this.size = size;
    }

    public int color() {
        return switch (state) {
            case PLAY -> 0xFF00FF00;
            case HANDSHAKING -> 0xFF808080;
            case LOGIN -> 0xFFFF0000;
            case STATUS -> 0xFFFFFF00;
            case CONFIGURATION -> 0xFFFFFFFF;
        };
    }

    public boolean outbound() {
        return outbound;
    }

    public ConnectionProtocol state() {
        return state;
    }

    public Packet<?> packet() {
        return packet;
    }

    public ResourceLocation channelId() {
        return channelId;
    }

    public FriendlyByteBuf wrappedBuf() {
        return wrappedBuf;
    }

    public long sentAt() {
        return sentAt;
    }

    public int size() {
        return size;
    }
}
