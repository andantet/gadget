package io.wispforest.gadget.testmod.client;

import io.wispforest.gadget.Gadget;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

public record EpicPacket(String maldenhagen) implements FabricPacket {
    public static final PacketType<EpicPacket> TYPE = PacketType.create(Gadget.id("epic"), EpicPacket::read);

    public static EpicPacket read(FriendlyByteBuf buf) {
        return new EpicPacket(buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(maldenhagen);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
