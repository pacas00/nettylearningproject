package net.petercashel.nettyCore.client;

import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packetCore.IPacketBase;
import net.petercashel.nettyCore.common.packetCore.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;

@SuppressWarnings("rawtypes")
public class ClientConnectionHander extends ChannelHandlerAdapter {
	private ByteBuf buf;
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(Packet.packetBufSize + Packet.packetHeaderSize); // (1)
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release(); // (1)
        buf = null;
    }
    
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m); // (2)
        m.release();

        if (buf.readableBytes() >= (Packet.packetBufSize + Packet.packetHeaderSize)) { // (3)
        	int side = buf.readInt();
        	Packet p = new Packet(buf.readInt(), buf.readBytes(Packet.packetBufSize));
            PacketRegistry.unpackExecute(p, ctx); 
        }
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof ReadTimeoutException) {
        	System.out.println("Connection Closed: " + ctx.channel().remoteAddress().toString());
        	clientCore.getChannel().close().sync();
        	PacketRegistry.shutdown();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

}
