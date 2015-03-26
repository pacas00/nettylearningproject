package net.petercashel.nettyCore.common;

import net.petercashel.nettyCore.common.packets.PingPacket;
import net.petercashel.nettyCore.common.packets.PingPongPacket;
import net.petercashel.nettyCore.common.packets.PongPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CorePackets {
	
	static short PingPacketID = 0;
	static short PongPacketID = 1;
	static short PingPongPacketID = 2;
	
	public static void registerCorePackets() {
		PacketRegistry.registerPacketWithID(PingPacketID, PingPacket.class);
		PacketRegistry.registerPacketWithID(PongPacketID, PongPacket.class);
		PacketRegistry.registerPacketWithID(PingPongPacketID, PingPongPacket.class);
	}
}
