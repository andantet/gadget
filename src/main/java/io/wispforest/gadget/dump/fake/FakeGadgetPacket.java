package io.wispforest.gadget.dump.fake;

import io.wispforest.gadget.dump.read.unwrapped.UnwrappedPacket;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

/**
 * Marks a fake packet that is not meant to actually be in the network.
 */
public interface FakeGadgetPacket extends Packet<PacketListener> {
    int id();

    void writeToDump(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side);

    default Packet<?> unwrapVanilla() {
        return this;
    }

    default @Nullable UnwrappedPacket unwrapGadget() {
        throw new UnsupportedOperationException("Unrenderable packet.");
    }

    @Override
    default void write(FriendlyByteBuf buf) {
        throw new IllegalStateException();
    }

    @Override
    default void handle(PacketListener listener) {
        throw new IllegalStateException();
    }

    @FunctionalInterface
    interface Reader<T extends FakeGadgetPacket> {
        T read(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side);
    }
}
