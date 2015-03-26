package net.petercashel.nettyCore.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface IPacketBase {

	public int getPacketID();
	public ByteBuf getPacket();
	public void setPacket(ByteBuf buf);
	public void pack();
	public void unpack();
	public void execute(ChannelHandlerContext ctx);

}
