package net.petercashel.nettyCore.server;

import java.net.SocketAddress;

import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packetCore.IPacketBase;
import net.petercashel.nettyCore.common.packetCore.Packet;
import net.petercashel.nettyCore.common.packets.PingPacket;
import net.petercashel.nettyCore.common.packets.PongPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;


public class ServerConnectionHandler extends ChannelHandlerAdapter {
	
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	if (!(serverCore.clientConnectionMap.containsKey(ctx.channel().remoteAddress()))) {
    		serverCore.clientConnectionMap.put(ctx.channel().remoteAddress(), ctx.channel());
    	}
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
            // do something
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

}
