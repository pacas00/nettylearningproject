package net.petercashel.nettyCore.common.packets;

import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packetCore.IPacketBase;
import net.petercashel.nettyCore.common.packetCore.Packet;
import net.petercashel.nettyCore.common.packetCore.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class PingPacket extends PacketBase implements IPacketBase {
	public PingPacket() {
	}

	public static int packetID = 0;
	
	@Override
	public void pack() {
		this.setPacket(this.getPacket());
		
	}

	@Override
	public void unpack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(ChannelHandlerContext ctx) {
		System.out.println("PING!");
		(PacketRegistry.pack(new PongPacket())).sendPacket(ctx);
		
		
	}

	@Override
	public int getPacketID() {
		// TODO Auto-generated method stub
		return packetID;
	}

	@Override
	public ByteBuf getPacket() {
		// TODO Auto-generated method stub
		ByteBuf b = Unpooled.buffer(Packet.packetBufSize).writeZero(Packet.packetBufSize);
		b.setIndex(0, 0);
		return b;
	}

	@Override
	public void setPacket(ByteBuf buf) {
		this.packet = buf;
		
	}
	
}