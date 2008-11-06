package simsim.core;

import java.awt.*;
import static simsim.core.Simulation.* ;

class Traffic implements Displayable {
	
	Traffic() {
		displayLivePackets = Globals.get("Traffic_DisplayLivePackets", true ) ;
		displayDeadPackets = Globals.get("Traffic_DisplayDeadPackets", false ) ;		
		displayLiveChannels = Globals.get("Traffic_DisplayLiveChannels", true ) ;
		displayDeadChannels = Globals.get("Traffic_DisplayDeadChannels", false ) ;		
		
		String deadPacketFilterMode = Globals.get("Traffic_DisplayDeadPacketsHistory", "time" ) ;
		filterDeadPackets = deadPacketFilterMode.equals("display") ;
	}
	
	private double lastDisplay = 0 ;
	public void display(Graphics2D gu, Graphics2D gs) {
		
		double now = Simulation.currentTime() ;
		
		if( displayDeadPackets )
			for (UdpPacket i : UdpPacket.deadUdpPackets)
				if( !filterDeadPackets || i.due >= lastDisplay )
					i.display(gu, gs);
			
		if( displayLivePackets )
			for (UdpPacket i : UdpPacket.liveUdpPackets)
				i.display(gu, gs);

		if( displayDeadChannels )
			for (TcpChannel i : TcpChannel.deadTcpChannels)
				i.display(gu, gs);

		if( displayLiveChannels )
			for (TcpChannel i : TcpChannel.liveTcpChannels)
				i.display(gu, gs);

		lastDisplay = now ;
	}
	
	boolean filterDeadPackets ;
	boolean displayLivePackets, displayDeadPackets ;
	boolean displayLiveChannels, displayDeadChannels ;
}
