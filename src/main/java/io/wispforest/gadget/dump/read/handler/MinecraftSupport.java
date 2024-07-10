package io.wispforest.gadget.dump.read.handler;

import io.wispforest.gadget.dump.read.unwrapped.LinesUnwrappedPacket;
import io.wispforest.gadget.util.ErrorSink;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class MinecraftSupport {
    public static final ResourceLocation REGISTER_CHANNEL = new ResourceLocation("minecraft", "register");

    public static final ResourceLocation UNREGISTER_CHANNEL = new ResourceLocation("minecraft", "unregister");

    private MinecraftSupport() {

    }

    public static void init() {
        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            if (!Objects.equals(packet.channelId(), BrandPayload.ID)) return null;

            FriendlyByteBuf buf = packet.wrappedBuf();
            String brand = buf.readUtf();

            return new BrandPacket(brand);
        });

        PacketUnwrapper.EVENT.register((packet, errSink) -> {
            boolean unregister;

            if (Objects.equals(packet.channelId(), REGISTER_CHANNEL)) {
                unregister = false;
            } else if (Objects.equals(packet.channelId(), UNREGISTER_CHANNEL)) {
                unregister = true;
            } else {
                return null;
            }

            FriendlyByteBuf buf = packet.wrappedBuf();
            StringBuilder more = new StringBuilder();
            List<ResourceLocation> channels = new ArrayList<>();

            while (buf.isReadable()) {
                byte next = buf.readByte();

                if (next != 0) {
                    more.append((char) next);
                } else {
                    channels.add(new ResourceLocation(more.toString()));

                    more = new StringBuilder();
                }
            }

            return new RegisterPacket(unregister, channels);
        });
    }

    public record BrandPacket(String brand) implements LinesUnwrappedPacket {
        @Override
        public void render(Consumer<Component> out, ErrorSink errSink) {
            out.accept(
                Component.literal("brand")
                    .append(Component.literal(" = \"" + brand + "\"")
                        .withStyle(ChatFormatting.GRAY)));
        }
    }

    public record RegisterPacket(boolean isUnregister, List<ResourceLocation> channels) implements LinesUnwrappedPacket {
        @Override
        public void render(Consumer<Component> out, ErrorSink errSink) {
            Component header = !isUnregister
                ? Component.literal("+ ")
                    .withStyle(ChatFormatting.GREEN)
                : Component.literal("- ")
                    .withStyle(ChatFormatting.RED);

            for (ResourceLocation channel : channels) {
                out.accept(
                    Component.literal("")
                        .append(header)
                        .append(Component.literal(channel.toString())
                            .withStyle(ChatFormatting.GRAY)));
            }
        }
    }
}
