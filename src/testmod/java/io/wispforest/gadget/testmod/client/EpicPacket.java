package io.wispforest.gadget.testmod.client;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.gadget.Gadget;
import net.minecraft.network.packet.CustomPayload;

public record EpicPacket(String maldenhagen) implements CustomPayload {
    public static final Id<EpicPacket> ID = new Id<>(Gadget.id("epic"));
    public static final Endec<EpicPacket> ENDEC = StructEndecBuilder.of(
        Endec.STRING.fieldOf("maldenhagen", EpicPacket::maldenhagen),
        EpicPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
