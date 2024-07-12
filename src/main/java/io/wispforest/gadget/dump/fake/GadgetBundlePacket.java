package io.wispforest.gadget.dump.fake;

import com.google.common.collect.Lists;
import io.wispforest.gadget.dump.write.PacketDumping;
import io.wispforest.gadget.util.NetworkUtil;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;

import java.util.ArrayList;
import java.util.List;

public record GadgetBundlePacket(NetworkPhase phase, NetworkSide side, List<Packet<?>> packets) implements FakeGadgetPacket {
    public static final int ID = -2;

    public static GadgetBundlePacket wrap(BundlePacket<?> bundle) {
        List<Packet<?>> packets = Lists.newArrayList(bundle.getPackets());

        if (bundle instanceof BundleS2CPacket) {
            return new GadgetBundlePacket(NetworkPhase.PLAY, NetworkSide.CLIENTBOUND, packets);
        } else {
            throw new IllegalStateException("Unknown bundle packet type " + bundle);
        }
    }

    public static GadgetBundlePacket read(PacketByteBuf buf, NetworkState<?> state) {
        int size = buf.readVarInt();
        List<Packet<?>> packets = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            PacketByteBuf subBuf = NetworkUtil.readOfLengthIntoTmp(buf);
            packets.add(PacketDumping.readPacket(subBuf, state));
        }

        return new GadgetBundlePacket(state.id(), state.side(), packets);
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public void writeToDump(PacketByteBuf buf, NetworkState<?> state) {
        buf.writeVarInt(packets.size());

        for (var subPacket : packets) {
            try (var ignored = NetworkUtil.writeByteLength(buf)) {
                PacketDumping.writePacket(buf, subPacket, state);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<?> unwrapVanilla() {
        if (phase == NetworkPhase.PLAY && side == NetworkSide.CLIENTBOUND) {
            // java i promise this cast is fine
            //noinspection RedundantCast (javac doesn't think this is redundant.)
            return new BundleS2CPacket((Iterable<Packet<? super ClientPlayPacketListener>>)(Object) packets);
        } else {
            throw new IllegalStateException("No such BundlePacket type for " + phase + " and " + side);
        }
    }
}
