package io.wispforest.gadget.dump.read.handler;

import io.wispforest.gadget.dump.read.unwrapped.FieldsUnwrappedPacket;
import io.wispforest.gadget.dump.read.unwrapped.LinesUnwrappedPacket;
import io.wispforest.gadget.mixin.owo.IndexedEndecAccessor;
import io.wispforest.gadget.mixin.owo.OwoNetChannelAccessor;
import io.wispforest.gadget.mixin.owo.ParticleSystemAccessor;
import io.wispforest.gadget.util.ErrorSink;
import io.wispforest.owo.particles.systems.ParticleSystem;
import io.wispforest.owo.particles.systems.ParticleSystemController;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import io.wispforest.owo.util.VectorSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;

public final class OwoSupport {
    public static final ResourceLocation HANDSHAKE_CHANNEL = new ResourceLocation("owo", "handshake");
    private static final Endec<Map<ResourceLocation, Integer>> HANDSHAKE_SERIALIZER = Endec.map(BuiltInEndecs.IDENTIFIER, Endec.INT);

    private OwoSupport() {

    }

    @SuppressWarnings("UnstableApiUsage")
    public static void init() {
        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            if (packet.state() != ConnectionProtocol.PLAY) return null;

            if (packet.channelId() == null) return null;

            OwoNetChannelAccessor channel = (OwoNetChannelAccessor) OwoNetChannelAccessor.getRegisteredChannels().get(packet.channelId());

            if (channel == null) return null;

            FriendlyByteBuf buf = packet.wrappedBuf();
            int netHandlerId = buf.readVarInt();
            int handlerId = netHandlerId;

            if (!packet.outbound())
                handlerId = -handlerId;

            IndexedEndecAccessor acc = channel.getEndecsByIndex().get(handlerId);

            if (acc == null) return null;

            Object unwrapped = buf.read(acc.getEndec());

            return new ChannelPacket(unwrapped, netHandlerId);
        });

        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            if (packet.state() != ConnectionProtocol.PLAY || packet.channelId() == null) return null;

            ParticleSystemController controller = ParticleSystemController.REGISTERED_CONTROLLERS.get(packet.channelId());

            if (controller == null) return null;

            FriendlyByteBuf buf = packet.wrappedBuf();
            int systemId = buf.readVarInt();
            Vec3 pos = VectorSerializer.read(buf);
            ParticleSystem<?> system = controller.systemsByIndex.get(systemId);
            Object data = buf.read(((ParticleSystemAccessor) system).getEndec());

            return new ParticleSystemPacket(controller, systemId, pos, data);
        });

        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            if (!(packet.packet() instanceof ClientboundCustomQueryPacket)
             || !Objects.equals(packet.channelId(), HANDSHAKE_CHANNEL))
                return null;

            FriendlyByteBuf buf = packet.wrappedBuf();

            return new HandshakeRequest(buf.isReadable()
                ? buf.read(HANDSHAKE_SERIALIZER)
                : Collections.emptyMap());
        });

        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            if (!(packet.packet() instanceof ServerboundCustomQueryAnswerPacket)
             || !Objects.equals(packet.channelId(), HANDSHAKE_CHANNEL))
                return null;

            FriendlyByteBuf buf = packet.wrappedBuf();

            Map<ResourceLocation, Integer> requiredChannels = buf.read(HANDSHAKE_SERIALIZER);
            Map<ResourceLocation, Integer> requiredControllers = buf.read(HANDSHAKE_SERIALIZER);

            Map<ResourceLocation, Integer> optionalChannels = Collections.emptyMap();

            if (buf.isReadable())
                optionalChannels = buf.read(HANDSHAKE_SERIALIZER);

            return new HandshakeResponse(requiredChannels, requiredControllers, optionalChannels);
        });

    }

    private static void drawHandshakeMap(Map<ResourceLocation, Integer> data, Component prefix, Consumer<Component> out) {
        for (var entry : data.entrySet()) {
            out.accept(Component.literal("")
                .append(prefix)
                .append(Component.literal(entry.getKey().toString())
                    .withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" = " + entry.getValue())
                    .withStyle(ChatFormatting.GRAY)));
        }
    }

    public record ParticleSystemPacket(ParticleSystemController controller, int systemId, Vec3 pos, Object data) implements FieldsUnwrappedPacket {
        @Override
        public Component headText() {
            return Component.translatable("text.gadget.particle_system", systemId, (int) pos.x, (int) pos.y, (int) pos.z);
        }

        @Override
        public @Nullable Object rawFieldsObject() {
            return data;
        }

        @Override
        public OptionalInt packetId() {
            return OptionalInt.of(systemId);
        }
    }

    public record ChannelPacket(Object packetData, int channelPacketId) implements FieldsUnwrappedPacket {
        @Override
        public @Nullable Object rawFieldsObject() {
            return packetData;
        }

        @Override
        public OptionalInt packetId() {
            return OptionalInt.of(channelPacketId);
        }
    }

    public record HandshakeRequest(Map<ResourceLocation, Integer> optionalChannels) implements LinesUnwrappedPacket {
        @Override
        public void render(Consumer<Component> out, ErrorSink errSink) {
            drawHandshakeMap(optionalChannels, Component.literal("o ").withStyle(ChatFormatting.AQUA), out);
        }
    }

    public record HandshakeResponse(Map<ResourceLocation, Integer> requiredChannels,
                                    Map<ResourceLocation, Integer> requiredControllers,
                                    Map<ResourceLocation, Integer> optionalChannels) implements LinesUnwrappedPacket {
        @Override
        public void render(Consumer<Component> out, ErrorSink errSink) {
            drawHandshakeMap(requiredChannels, Component.literal("r ").withStyle(ChatFormatting.RED), out);
            drawHandshakeMap(requiredControllers, Component.literal("p ").withStyle(ChatFormatting.GREEN), out);
            drawHandshakeMap(optionalChannels, Component.literal("o ").withStyle(ChatFormatting.AQUA), out);
        }
    }
}
