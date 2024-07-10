package io.wispforest.gadget.dump.read.handler;

import io.wispforest.gadget.dump.read.unwrapped.FieldsUnwrappedPacket;
import io.wispforest.gadget.dump.read.unwrapped.LinesUnwrappedPacket;
import io.wispforest.gadget.network.FabricPacketHacks;
import io.wispforest.gadget.util.ErrorSink;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class FapiSupport {
    public static final ResourceLocation EARLY_REGISTRATION_CHANNEL = new ResourceLocation("fabric-networking-api-v1", "early_registration");

    private FapiSupport() {

    }

    public static void init() {
        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            PacketType<?> type = FabricPacketHacks.getForId(packet.channelId());

            if (type == null) return null;

            FriendlyByteBuf buf = packet.wrappedBuf();
            Object unwrapped = type.read(buf);

            return new FabricObjectPacket(unwrapped);
        });

        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            if (!Objects.equals(packet.channelId(), EARLY_REGISTRATION_CHANNEL)) return null;

            FriendlyByteBuf buf = packet.wrappedBuf();
            List<ResourceLocation> channels = buf.readList(FriendlyByteBuf::readResourceLocation);

            return new EarlyRegisterPacket(channels);
        });
    }

    public record FabricObjectPacket(Object unwrapped) implements FieldsUnwrappedPacket {
        @Override
        public @Nullable Object rawFieldsObject() {
            return unwrapped;
        }
    }

    public record EarlyRegisterPacket(List<ResourceLocation> channels) implements LinesUnwrappedPacket {
        @Override
        public void render(Consumer<Component> out, ErrorSink errSink) {
            for (ResourceLocation channel : channels) {
                out.accept(
                    Component.literal("+ ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(channel.toString())
                            .withStyle(ChatFormatting.GRAY)));
            }
        }
    }
}
