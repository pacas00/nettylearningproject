package net.petercashel.nettyCore.client;

import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.petercashel.commonlib.threading.threadManager;
import net.petercashel.nettyCore.common.PacketRegistry;
import net.petercashel.nettyCore.common.packets.PingPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class clientCore {

	static final boolean SSL = System.getProperty("ssl") != null;
	private static Channel connection;
	static final int side = 1;
	static String _host = "";
	static int _port = 0;

	private static void initaliseConnection() throws Exception{
		initaliseConnection(_host, _port);
	}

	public static void initaliseConnection (final String addr, final int port) throws Exception {
		_host = addr;
		_port = port;
		PacketRegistry.setupRegistry();
		PacketRegistry.Side = side;
		
		final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					
					p.addLast("ssl", sslCtx.newHandler(ch.alloc(), addr, port));
					p.addLast("readTimeoutHandler", new ReadTimeoutHandler(30));
					p.addLast("InboundOutboundClientHandler", new ClientConnectionHander());
				}
			});

			// Make the connection attempt.
			ChannelFuture f = b.connect(addr, port).sync();

			f.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);


			if (!f.isSuccess()) throw new RuntimeException("Failed to connect");
			// if a wait option was selected and the connect did not fail,
			// the Date can now be sent.
			connection = f.channel();
			System.out.println("Client Core Connected!");
			
			// Initiate the Ping->Pong->PingPong Packet test.
			PacketRegistry.pack(new PingPacket()).sendPacket(connection.pipeline().context("InboundOutboundClientHandler"));

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}

	}
	
	
	public static Channel getChannel() {
		//Check if closed, if so, check if host/port are set, if so, reconnect
		if (!connection.isActive()) {
			if (_host != null && _port != 0) {
				threadManager.getInstance().addRunnable(
				new Runnable() {
					@Override
					public void run() {
						try {
							clientCore.initaliseConnection();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		}
		return connection;
	}
}
