package me.basiqueevangelist.gadget.client.dump;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.basiqueevangelist.gadget.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PacketDumpReader {
    private PacketDumpReader() {

    }

    public static List<DumpedPacket> readAll(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        List<DumpedPacket> list = new ArrayList<>();
        PacketByteBuf buf = PacketByteBufs.create();

        Int2ObjectMap<Identifier> loginQueryChannels = new Int2ObjectOpenHashMap<>();

        try {
            // I know, IntelliJ.
            //noinspection InfiniteLoopStatement
            while (true) {
                int length = dis.readInt();

                buf.readerIndex(0);
                buf.writerIndex(0);

                buf.writeBytes(dis.readNBytes(length));

                short flags = buf.readShort();
                boolean outbound = (flags & 1) != 0;
                NetworkState state = switch (flags & 0b0110) {
                    case 0b0000 -> NetworkState.HANDSHAKING;
                    case 0b0010 -> NetworkState.PLAY;
                    case 0b0100 -> NetworkState.STATUS;
                    case 0b0110 -> NetworkState.LOGIN;
                    default -> throw new IllegalStateException();
                };
                int packetId = buf.readVarInt();
                Packet<?> packet = state.getPacketHandler(outbound ? NetworkSide.SERVERBOUND : NetworkSide.CLIENTBOUND, packetId, buf);
                Identifier channelId = NetworkUtil.getChannelOrNull(packet);

                if (packet instanceof LoginQueryRequestS2CPacket req) {
                    loginQueryChannels.put(req.getQueryId(), req.getChannel());
                } else if (packet instanceof LoginQueryResponseC2SPacket res) {
                    channelId = loginQueryChannels.get(res.getQueryId());
                }

                list.add(new DumpedPacket(outbound, state, packet, channelId));
            }
        } catch (EOFException e) {
            return list;
        }
    }
}
