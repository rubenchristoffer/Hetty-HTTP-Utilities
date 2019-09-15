package rubenchristoffer.hetty.filters;

import java.net.URL;

import rubenchristoffer.hetty.HTTPPacket;

public abstract class HTTPPacketFilter {
	
	protected String name;
	
	public HTTPPacketFilter () {}
	
	public HTTPPacketFilter (String name) {
		this.name = name;
	}
	
	public abstract HTTPPacket Filter (HTTPPacket packet, URL url);
	
	public String GetName () {
		return name;
	}
	
	public abstract HTTPPacketFilter Clone ();
	
}