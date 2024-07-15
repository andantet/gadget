package io.wispforest.gadget.util;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public final class NetworkUtil {
    private static final ThreadLocal<Supplier<PacketByteBuf>> TMP_BUF_SOURCE = ThreadLocal.withInitial(() -> SupplierUtil.weakLazy(PacketByteBufs::create));

    private NetworkUtil() {

    }

    public static Identifier getChannelOrNull(Packet<?> packet) {
        return switch (packet) {
            case CustomPayloadS2CPacket pkt -> pkt.payload().getId().id();
            case CustomPayloadC2SPacket pkt -> pkt.payload().getId().id();
            case LoginQueryRequestS2CPacket pkt -> pkt.payload().id();
            case null, default -> null;
        };
    }

    public static Object unwrapCustom(Packet<?> packet) {
        return switch (packet) {
            case CustomPayloadS2CPacket pkt -> pkt.payload();
            case CustomPayloadC2SPacket pkt -> pkt.payload();
            case LoginQueryRequestS2CPacket pkt -> pkt.payload();
            case LoginQueryResponseC2SPacket pkt when pkt.response() != null -> pkt.response();
            case null, default -> null;
        };
    }

    public static InfallibleClosable resetIndexes(ByteBuf buf) {
        int readerIdx = buf.readerIndex();
        int writerIdx = buf.writerIndex();

        return () -> {
            buf.readerIndex(readerIdx);
            buf.writerIndex(writerIdx);
        };
    }

    public static InfallibleClosable writeByteLength(PacketByteBuf buf) {
        int idIdx = buf.writerIndex();
        buf.writeInt(0);
        int startIdx = buf.writerIndex();

        return () -> {
            int endIdx = buf.writerIndex();
            buf.writerIndex(idIdx);
            buf.writeInt(endIdx - startIdx);
            buf.writerIndex(endIdx);
        };
    }

    public static PacketByteBuf readOfLengthIntoTmp(PacketByteBuf buf) {
        PacketByteBuf tmpBuf = TMP_BUF_SOURCE.get().get();
        int length = buf.readInt();

        tmpBuf.readerIndex(0);
        tmpBuf.writerIndex(0);
        tmpBuf.writeBytes(buf, length);

        return tmpBuf;
    }
}
