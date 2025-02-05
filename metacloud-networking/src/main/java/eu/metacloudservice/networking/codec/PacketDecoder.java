package eu.metacloudservice.networking.codec;

import eu.metacloudservice.networking.NettyDriver;
import eu.metacloudservice.networking.packet.NettyBuffer;
import eu.metacloudservice.networking.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.SneakyThrows;
import lombok.var;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @SneakyThrows
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        int packetUUID = in.readInt();
        if (NettyDriver.getInstance().getPacketDriver().getPacket(packetUUID) != null){
            var packetClass = NettyDriver.getInstance().getPacketDriver().getPacket(packetUUID);

            if (packetClass != null) {
                ByteBuf payload = in.readBytes(in.readableBytes());
                NettyBuffer nettyBuffer = new NettyBuffer(payload);
                Packet packet = packetClass.getDeclaredConstructor().newInstance();
                packet.setPacketUUID(packetUUID);
                packet.readPacket(nettyBuffer);
                out.add(packet);
                NettyDriver.getInstance().getPacketDriver().handle(packetUUID, ctx.channel(), packet);
            }
        }
    }
}
