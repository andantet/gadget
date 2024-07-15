package io.wispforest.gadget.dump.fake;

import io.netty.buffer.ByteBuf;
import io.wispforest.gadget.dump.read.unwrapped.UnwrappedPacket;
import io.wispforest.gadget.util.ThrowableUtil;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record GadgetWriteErrorPacket(int packetId, String exceptionText) implements FakeGadgetPacket {
    public static final int ID = -1;
    public static final PacketCodec<ByteBuf, GadgetWriteErrorPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.VAR_INT, GadgetWriteErrorPacket::packetId,
        PacketCodecs.STRING, GadgetWriteErrorPacket::exceptionText,
        GadgetWriteErrorPacket::new
    );

    public static GadgetWriteErrorPacket fromThrowable(int packetId, Throwable t) {
        return new GadgetWriteErrorPacket(packetId, ThrowableUtil.throwableToString(t));
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public PacketCodec<ByteBuf, GadgetWriteErrorPacket> codec() {
        return CODEC;
    }

    @Override
    public UnwrappedPacket unwrapGadget() {
        // Don't render anything.
        return UnwrappedPacket.NULL;
    }
}
