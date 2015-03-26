package net.petercashel.nettyCore.common.packet;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

import net.petercashel.commonlib.threading.threadManager;

public class PacketRegistry {

	private static volatile HashMap<Integer,Class<? extends IPacketBase>> packets = new HashMap<Integer,Class<? extends IPacketBase>>();

	public static synchronized int registerPacket(Class<? extends IPacketBase> clazz ) {
		int id = (packets.size() + 1);
		packets.put(id, clazz);
		return id;
	}

	protected static synchronized int registerPacketWithID(int id, Class<? extends IPacketBase> clazz ) {
		packets.put(id, clazz);
		return id;
	}

	private static boolean setup = false;
	private static threadManager threadMan;
	public static void setupRegistry() {
		if (setup) return;
		setup = true;
		CorePackets.registerCorePackets();
		threadMan = new threadManager();
	}

	public static IPacketBase unpack(Packet p) {
		Class<? extends IPacketBase> packetClass = packets.get(p.packetID);
		IPacketBase packet = null;
		try {
			packet = packetClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		packet.setPacket(p.packet);
		packet.unpack();
		
		return packet;
	}
	
	public static Packet pack (IPacketBase p) {
		p.pack();
		return new Packet(p.getPacketID(), p.getPacket());
	}

	public static void unpackExecute(final Packet p, final ChannelHandlerContext ctx) {
		Runnable r = new Runnable() { 
		    public void run() {
		    	Class<? extends IPacketBase> packetClass = packets.get(p.packetID);
				IPacketBase packet = null;
				try {
					packet = packetClass.newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				packet.setPacket(p.packet);
				packet.unpack();
				packet.execute(ctx);
		    }
		  };
		  threadMan.addRunnable(r);
		
	}

	public static int Side = -1;
	public static int GetOtherSide() {
		if (Side == 0) return 1;
		if (Side == 1) return 0;
		return 2;
	}



}
