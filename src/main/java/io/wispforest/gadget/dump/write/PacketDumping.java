package io.wispforest.gadget.dump.write;

import io.netty.buffer.ByteBuf;
import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.dump.fake.*;
import io.wispforest.gadget.util.SlicingPacketByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;

public final class PacketDumping {
    private static final Int2ObjectMap<FakeGadgetPacket.Reader<?>> PACKETS = new Int2ObjectOpenHashMap<>();

    private PacketDumping() {

    }

    static {
        register(GadgetWriteErrorPacket.ID, GadgetWriteErrorPacket::read);
//        register(GadgetReadErrorPacket.ID, GadgetReadErrorPacket::read);

    }

    public static void register(int id, FakeGadgetPacket.Reader<?> reader) {
        if (PACKETS.put(id, reader) != null) {
            throw new IllegalStateException("This reader on " + id + " collides with another reader");
        }
    }

    @SuppressWarnings("unchecked")
    public static void writePacket(PacketByteBuf buf, Packet<?> packet, NetworkState<?> state) {
        int startWriteIdx = buf.writerIndex();
        int packetId = 0;

        try {
            if (packet instanceof FakeGadgetPacket fakePacket) {
                packetId = fakePacket.id();
                buf.writeVarInt(packetId);
                fakePacket.writeToDump(buf, state);
                return;
            }

            ((PacketCodec<ByteBuf, Object>)(Object) state.codec()).encode(buf, packet);
        } catch (Exception e) {
            buf.writerIndex(startWriteIdx);

            Gadget.LOGGER.error("Error while writing packet {}", packet, e);

            GadgetWriteErrorPacket writeError = GadgetWriteErrorPacket.fromThrowable(packetId, e);
            buf.writeVarInt(writeError.id());
            writeError.writeToDump(buf, state);
        }
    }

    public static Packet<?> readPacket(PacketByteBuf buf, NetworkState<?> state) {
        int startOfData = buf.readerIndex();
        int packetId = buf.readVarInt();

        try {
            FakeGadgetPacket.Reader<?> fakeReader = PACKETS.get(packetId);
            if (fakeReader != null) {
                return fakeReader.read(buf, state).unwrapVanilla();
            }

            buf.readerIndex(startOfData);

            return state.codec().decode(buf);
        } catch (Exception e) {
            buf.readerIndex(startOfData);
            return GadgetReadErrorPacket.from(buf, packetId, e);
        }
    }
}
