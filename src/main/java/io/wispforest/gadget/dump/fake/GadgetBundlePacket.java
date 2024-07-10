package io.wispforest.gadget.dump.fake;

import com.google.common.collect.Lists;
import io.wispforest.gadget.dump.write.PacketDumping;
import io.wispforest.gadget.util.NetworkUtil;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;

import java.util.ArrayList;
import java.util.List;

public record GadgetBundlePacket(ConnectionProtocol state, PacketFlow side, List<Packet<?>> packets) implements FakeGadgetPacket {
    public static final int ID = -2;

    public static GadgetBundlePacket wrap(BundlePacket<?> bundle) {
        List<Packet<?>> packets = Lists.newArrayList(bundle.subPackets());

        if (bundle instanceof ClientboundBundlePacket) {
            return new GadgetBundlePacket(ConnectionProtocol.PLAY, PacketFlow.CLIENTBOUND, packets);
        } else {
            throw new IllegalStateException("Unknown bundle packet type " + bundle);
        }
    }

    public static GadgetBundlePacket read(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        int size = buf.readVarInt();
        List<Packet<?>> packets = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            FriendlyByteBuf subBuf = NetworkUtil.readOfLengthIntoTmp(buf);
            packets.add(PacketDumping.readPacket(subBuf, state, side));
        }

        return new GadgetBundlePacket(state, side, packets);
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public void writeToDump(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        buf.writeVarInt(packets.size());

        for (var subPacket : packets) {
            try (var ignored = NetworkUtil.writeByteLength(buf)) {
                PacketDumping.writePacket(buf, subPacket, state, side);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<?> unwrapVanilla() {
        if (state == ConnectionProtocol.PLAY && side == PacketFlow.CLIENTBOUND) {
            // java i promise this cast is fine
            return new ClientboundBundlePacket((Iterable<Packet<ClientGamePacketListener>>)(Object) packets);
        } else {
            throw new IllegalStateException("No such BundlePacket type for " + state + " and " + side);
        }
    }
}
