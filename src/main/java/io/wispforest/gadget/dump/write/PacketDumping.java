package io.wispforest.gadget.dump.write;

import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.dump.fake.*;
import io.wispforest.gadget.util.SlicingFriendlyByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;

public final class PacketDumping {
    private static final Int2ObjectMap<FakeGadgetPacket.Reader<?>> PACKETS = new Int2ObjectOpenHashMap<>();

    private PacketDumping() {

    }

    static {
        register(GadgetWriteErrorPacket.ID, GadgetWriteErrorPacket::read);
        register(GadgetBundlePacket.ID, GadgetBundlePacket::read);
//        register(GadgetReadErrorPacket.ID, GadgetReadErrorPacket::read);
        register(GadgetRecipesS2CPacket.ID, GadgetRecipesS2CPacket::read);
    }

    public static void register(int id, FakeGadgetPacket.Reader<?> reader) {
        if (PACKETS.put(id, reader) != null) {
            throw new IllegalStateException("This reader on " + id + " collides with another reader");
        }
    }

    public static void writePacket(FriendlyByteBuf buf, Packet<?> packet, ConnectionProtocol state, PacketFlow side) {
        int startWriteIdx = buf.writerIndex();
        int packetId = 0;

        try {
            if (packet instanceof BundlePacket<?> bundle) {
                packet = GadgetBundlePacket.wrap(bundle);
            }

            if (packet instanceof ClientboundUpdateRecipesPacket recipes) {
                packet = new GadgetRecipesS2CPacket(recipes.getRecipes());
            }

            if (packet instanceof FakeGadgetPacket fakePacket) {
                packetId = fakePacket.id();
                buf.writeVarInt(packetId);
                fakePacket.writeToDump(buf, state, side);
                return;
            }

            packetId = state.codec(side).packetId(packet);

            if (packetId == -1)
                throw new UnsupportedOperationException(packet.getClass().getName() + " is an invalid packet in " + side + " " + state);

            buf.writeVarInt(packetId);

            packet.write(new SlicingFriendlyByteBuf(buf));
        } catch (Exception e) {
            buf.writerIndex(startWriteIdx);

            Gadget.LOGGER.error("Error while writing packet {}", packet, e);

            GadgetWriteErrorPacket writeError = GadgetWriteErrorPacket.fromThrowable(packetId, e);
            buf.writeVarInt(writeError.id());
            writeError.writeToDump(buf, state, side);
        }
    }

    public static Packet<?> readPacket(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        int startOfData = buf.readerIndex();
        int packetId = buf.readVarInt();

        try {
            FakeGadgetPacket.Reader<?> fakeReader = PACKETS.get(packetId);
            if (fakeReader != null) {
                return fakeReader.read(buf, state, side).unwrapVanilla();
            }

            return state.codec(side).createPacket(packetId, buf);
        } catch (Exception e) {
            buf.readerIndex(startOfData);
            return GadgetReadErrorPacket.from(buf, packetId, e);
        }
    }
}
