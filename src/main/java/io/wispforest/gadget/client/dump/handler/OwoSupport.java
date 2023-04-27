package io.wispforest.gadget.client.dump.handler;

import io.wispforest.gadget.client.field.FieldDataIsland;
import io.wispforest.gadget.field.LocalFieldDataSource;
import io.wispforest.gadget.mixin.owo.ParticleSystemAccessor;
import io.wispforest.gadget.util.NetworkUtil;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.particles.systems.ParticleSystem;
import io.wispforest.owo.particles.systems.ParticleSystemController;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.util.VectorSerializer;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public final class OwoSupport {
    public static final Identifier HANDSHAKE_CHANNEL = new Identifier("owo", "handshake");

    @SuppressWarnings("unchecked")
    private static final PacketBufSerializer<Map<Identifier, Integer>> HANDSHAKE_SERIALIZER =
        (PacketBufSerializer<Map<Identifier, Integer>>) (Object) PacketBufSerializer.createMapSerializer(Map.class, Identifier.class, Integer.class);

    private OwoSupport() {

    }

    public static void init() {
        PacketRenderer.EVENT.register((packet, view, errSink) -> {
            if (packet.state() != NetworkState.PLAY) return false;

            if (packet.channelId() == null) return false;

            ParticleSystemController controller = ParticleSystemController.REGISTERED_CONTROLLERS.get(packet.channelId());

            if (controller == null) return false;

            PacketByteBuf buf = NetworkUtil.unwrapCustom(packet.packet());
            int systemId = buf.readVarInt();
            Vec3d pos = VectorSerializer.read(buf);

            view.child(Components.label(Text.translatable("text.gadget.particle_system", systemId, (int) pos.x, (int) pos.y, (int) pos.z)));

            ParticleSystem<?> system = controller.systemsByIndex.get(systemId);
            Object data = ((ParticleSystemAccessor) system).getAdapter().deserializer().apply(buf);

            if (data != null) {
                FieldDataIsland island = new FieldDataIsland(
                    new LocalFieldDataSource(data, false),
                    true,
                    false
                );
                view.child(island.mainContainer());
            }

            return true;
        });

        PacketRenderer.EVENT.register((packet, view, errSink) -> {
            if (!(packet.packet() instanceof LoginQueryRequestS2CPacket) || !Objects.equals(packet.channelId(), HANDSHAKE_CHANNEL)) return false;

            PacketByteBuf buf = NetworkUtil.unwrapCustom(packet.packet());

            if (buf.isReadable()) {
                Map<Identifier, Integer> optionalChannels = HANDSHAKE_SERIALIZER.deserializer().apply(buf);

                drawHandshakeMap(optionalChannels, Text.literal("o ").formatted(Formatting.AQUA), view);
            }

            return true;
        });

        PacketRenderer.EVENT.register((packet, view, errSink) -> {
            if (!(packet.packet() instanceof LoginQueryResponseC2SPacket) || !Objects.equals(packet.channelId(), HANDSHAKE_CHANNEL)) return false;

            PacketByteBuf buf = NetworkUtil.unwrapCustom(packet.packet());

            Map<Identifier, Integer> requiredChannels = HANDSHAKE_SERIALIZER.deserializer().apply(buf);
            Map<Identifier, Integer> requiredControllers = HANDSHAKE_SERIALIZER.deserializer().apply(buf);

            drawHandshakeMap(requiredChannels, Text.literal("r ").formatted(Formatting.RED), view);
            drawHandshakeMap(requiredControllers, Text.literal("p ").formatted(Formatting.GREEN), view);

            if (buf.isReadable()) {
                Map<Identifier, Integer> optionalChannels = HANDSHAKE_SERIALIZER.deserializer().apply(buf);

                drawHandshakeMap(optionalChannels, Text.literal("o ").formatted(Formatting.AQUA), view);
            }

            return true;
        });

        // TODO: OwO handshake and config sync.
    }

    private static void drawHandshakeMap(Map<Identifier, Integer> data, Text prefix, FlowLayout view) {
        for (var entry : data.entrySet()) {
            view.child(Components.label(
                    Text.literal("")
                        .append(prefix)
                        .append(Text.literal(entry.getKey().toString())
                            .formatted(Formatting.WHITE))
                        .append(Text.literal(" = " + entry.getValue())
                            .formatted(Formatting.GRAY)))
                .margins(Insets.bottom(3)));
        }
    }
}