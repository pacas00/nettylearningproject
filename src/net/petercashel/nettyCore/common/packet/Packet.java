package net.petercashel.nettyCore.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class Packet extends PacketBase implements IPacketBase{
	
	// packetID is 2 bytes, so total packet size is packetBufSize + 4 bytes.
	public static final int packetBufSize = 32768;
	public static final int packetHeaderSize = 8;
	
	public Packet(int i, ByteBuf buf) {
		this.packetID = i;
		this.packet = buf;
	}

	@Override
	public int getPacketID() {
		// TODO Auto-generated method stub
		return packetID;
	}

	@Override
	public ByteBuf getPacket() {
		// TODO Auto-generated method stub
		return packet;
	}

	@Override
	public void pack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unpack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPacket(ByteBuf buf) {
		this.packet = buf;
		
	}	
}
