package io.wispforest.gadget.dump.fake;

import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record GadgetDynamicRegistriesPacket(Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> registries) implements FakeGadgetPacket {
    public static final int ID = -3;

    private static final PacketCodec<ByteBuf, RegistryKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = Identifier.PACKET_CODEC
        .xmap(RegistryKey::ofRegistry, RegistryKey::getValue);

    public static final PacketCodec<ByteBuf, GadgetDynamicRegistriesPacket> CODEC = PacketCodecs.<ByteBuf, RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>, Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>>>map(
            HashMap::new,
            REGISTRY_KEY_CODEC,
            SerializableRegistries.SerializedRegistryEntry.PACKET_CODEC.collect(PacketCodecs.toList())
        )
        .xmap(GadgetDynamicRegistriesPacket::new, GadgetDynamicRegistriesPacket::registries);

    public static GadgetDynamicRegistriesPacket fromRegistries(DynamicRegistryManager registries) {
        DynamicOps<NbtElement> dynamicOps = registries.getOps(NbtOps.INSTANCE);
        Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> map = new HashMap<>();

        SerializableRegistries.forEachSyncedRegistry(
            dynamicOps,
            registries,
            Set.of(),
            map::put
        );

        return new GadgetDynamicRegistriesPacket(map);
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public PacketCodec<ByteBuf, GadgetDynamicRegistriesPacket> codec() {
        return CODEC;
    }

    @Override
    public boolean isVirtual() {
        return true;
    }
}
