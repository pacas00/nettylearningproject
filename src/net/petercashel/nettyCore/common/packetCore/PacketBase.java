package net.petercashel.nettyCore.common.packetCore;

import net.petercashel.nettyCore.common.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketBase {
	
	public static int packetID = -1;
	public ByteBuf packet;
	
	public void sendPacket(ChannelHandlerContext ctx) {
		ByteBuf b = ctx.alloc().buffer(Packet.packetBufSize + Packet.packetHeaderSize, Packet.packetBufSize + Packet.packetHeaderSize);
		b.writeInt(PacketRegistry.GetOtherSide());
		b.writeInt(packetID);
		b.writeBytes(packet);
		if (b.readableBytes() == (Packet.packetBufSize + Packet.packetHeaderSize)) {
			ctx.writeAndFlush(b);
		} else if (b.readableBytes() > (Packet.packetBufSize + Packet.packetHeaderSize)) {
			System.out.println("INVALID PACKET! DISCARDING!");
		} else {
			b.writeZero(b.writableBytes());
			
			if (b.readableBytes() == (Packet.packetBufSize + Packet.packetHeaderSize)) {
				ctx.writeAndFlush(b);
			} else if (b.readableBytes() > (Packet.packetBufSize + Packet.packetHeaderSize)) {
				System.out.println("INVALID PACKET! DISCARDING!");
			}
		}
		
	}
}
