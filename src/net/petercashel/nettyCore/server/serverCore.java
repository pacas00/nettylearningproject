package net.petercashel.nettyCore.server;


import java.net.SocketAddress;
import java.util.HashMap;

import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packets.PongPacket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class serverCore {

	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));
	static final int side = 0;
	static HashMap<SocketAddress,Channel> clientConnectionMap = new HashMap<SocketAddress,Channel>();

	// http://netty.io/wiki/user-guide-for-4.x.html
	public static void initaliseServer() throws Exception {

		PacketRegistry.setupRegistry();
		PacketRegistry.Side = side;

		SelfSignedCertificate ssc = new SelfSignedCertificate();
        final SslContext sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class) // (3)
			.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast("ssl", sslCtx.newHandler(ch.alloc()));
					p.addLast("readTimeoutHandler", new ReadTimeoutHandler(30));
					p.addLast("InboundOutboundServerHandler", new ServerConnectionHandler());
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)          // (5)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.TCP_NODELAY, true); // (6)
			

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind("0.0.0.0", PORT).sync(); // (7)
			System.out.println("Server Core Initalised!");
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}
}
