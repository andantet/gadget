package io.wispforest.gadget.dump.fake;

import io.wispforest.gadget.dump.read.unwrapped.UnwrappedPacket;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import org.jetbrains.annotations.Nullable;

/**
 * Marks a fake packet that is not meant to actually be in the network.
 */
public interface FakeGadgetPacket extends Packet<PacketListener> {
    int id();

    void writeToDump(PacketByteBuf buf, NetworkState<?> state);

    default Packet<?> unwrapVanilla() {
        return this;
    }

    default @Nullable UnwrappedPacket unwrapGadget() {
        throw new UnsupportedOperationException("Unrenderable packet.");
    }

    @Override
    default void apply(PacketListener listener) {
        throw new IllegalStateException();
    }

    @Override
    default PacketType<? extends Packet<PacketListener>> getPacketId() {
        throw new IllegalStateException();
    }

    @FunctionalInterface
    interface Reader<T extends FakeGadgetPacket> {
        T read(PacketByteBuf buf, NetworkState<?> state);
    }
}
