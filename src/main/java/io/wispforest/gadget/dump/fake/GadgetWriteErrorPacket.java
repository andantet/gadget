package io.wispforest.gadget.dump.fake;

import io.wispforest.gadget.dump.read.unwrapped.UnwrappedPacket;
import io.wispforest.gadget.util.ThrowableUtil;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;

public record GadgetWriteErrorPacket(int packetId, String exceptionText) implements FakeGadgetPacket {
    public static final int ID = -1;

    public static GadgetWriteErrorPacket fromThrowable(int packetId, Throwable t) {
        return new GadgetWriteErrorPacket(packetId, ThrowableUtil.throwableToString(t));
    }

    public static GadgetWriteErrorPacket read(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        return new GadgetWriteErrorPacket(buf.readVarInt(), buf.readUtf());
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public void writeToDump(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        buf.writeVarInt(packetId);
        buf.writeUtf(exceptionText);
    }

    @Override
    public UnwrappedPacket unwrapGadget() {
        // Don't render anything.
        return UnwrappedPacket.NULL;
    }
}
