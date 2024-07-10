package io.wispforest.gadget.dump.fake;

import io.wispforest.gadget.dump.read.unwrapped.UnprocessedUnwrappedPacket;
import io.wispforest.gadget.dump.read.unwrapped.UnwrappedPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;

public record GadgetReadErrorPacket(byte[] data, int packetId, Exception exception) implements FakeGadgetPacket {
    public static final int ID = -3;

    public static GadgetReadErrorPacket from(FriendlyByteBuf buf, int packetId, Exception exception) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);

        return new GadgetReadErrorPacket(data, packetId, exception);
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public void writeToDump(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnwrappedPacket unwrapGadget() {
        return new UnprocessedUnwrappedPacket(data);
    }
}
