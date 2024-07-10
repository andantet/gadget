package io.wispforest.gadget.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.wispforest.gadget.dump.read.DumpedPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class NetworkUtil {
    private static final ThreadLocal<Supplier<FriendlyByteBuf>> TMP_BUF_SOURCE = ThreadLocal.withInitial(() -> SupplierUtil.weakLazy(PacketByteBufs::create));

    private NetworkUtil() {

    }

    public static ResourceLocation getChannelOrNull(Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket pkt)
            return pkt.payload().id();
        else if (packet instanceof ServerboundCustomPayloadPacket pkt)
            return pkt.payload().id();
        else if (packet instanceof ClientboundCustomQueryPacket pkt)
            return pkt.payload().id();
        else
            return null;
    }

    public static FriendlyByteBuf unwrapCustom(Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket pkt) {
            FriendlyByteBuf serializeBuffer = new FriendlyByteBuf(Unpooled.buffer());
            pkt.payload().write(new SlicingFriendlyByteBuf(serializeBuffer));
            return serializeBuffer;
        } else if (packet instanceof ServerboundCustomPayloadPacket pkt) {
            FriendlyByteBuf serializeBuffer = new FriendlyByteBuf(Unpooled.buffer());
            pkt.payload().write(new SlicingFriendlyByteBuf(serializeBuffer));
            return serializeBuffer;
        } else if (packet instanceof ClientboundCustomQueryPacket pkt) {
            FriendlyByteBuf serializeBuffer = new FriendlyByteBuf(Unpooled.buffer());
            pkt.payload().write(new SlicingFriendlyByteBuf(serializeBuffer));
            return serializeBuffer;
        } else if (packet instanceof ServerboundCustomQueryAnswerPacket pkt && pkt.payload() != null) {
            FriendlyByteBuf serializeBuffer = new FriendlyByteBuf(Unpooled.buffer());
            pkt.payload().write(new SlicingFriendlyByteBuf(serializeBuffer));
            return serializeBuffer;
        } else {
            return null;
        }
    }

    public static InfallibleClosable resetIndexes(DumpedPacket packet) {
        if (packet.wrappedBuf() == null) {
            return () -> { };
        } else {
            return resetIndexes(packet.wrappedBuf());
        }
    }

    public static InfallibleClosable resetIndexes(ByteBuf buf) {
        int readerIdx = buf.readerIndex();
        int writerIdx = buf.writerIndex();

        return () -> {
            buf.readerIndex(readerIdx);
            buf.writerIndex(writerIdx);
        };
    }

    public static InfallibleClosable writeByteLength(FriendlyByteBuf buf) {
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

    public static FriendlyByteBuf readOfLengthIntoTmp(FriendlyByteBuf buf) {
        FriendlyByteBuf tmpBuf = TMP_BUF_SOURCE.get().get();
        int length = buf.readInt();

        tmpBuf.readerIndex(0);
        tmpBuf.writerIndex(0);
        tmpBuf.writeBytes(buf, length);

        return tmpBuf;
    }
}
