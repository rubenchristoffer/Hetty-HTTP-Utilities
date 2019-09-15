package rubenchristoffer.hetty.filters;

import java.net.URL;

import rubenchristoffer.hetty.HTTPPacket;

/**
 * Sets value of 'Connection' header to 'Keep-Alive'.
 * Sets value of 'User-Agent' header to userAgent provided. 
 * @author HallaBalla
 *
 */
public class HTTPBasicHeadersFilter extends HTTPPacketFilter {
	
	private String userAgent;
	private Mode mode;
	
	public enum Mode {
		All,
		OnlyUserAgent,
		OnlyConnection
	}
	
	public HTTPBasicHeadersFilter (String userAgent, Mode mode) {
		super();
		
		this.userAgent = userAgent;
		this.mode = mode;
	}
	
	public HTTPBasicHeadersFilter (String userAgent, Mode mode, String name) {
		super (name);
		
		this.userAgent = userAgent;
		this.mode = mode;
	}
	
	@Override
	public HTTPPacket Filter(HTTPPacket packet, URL url) {
		if (mode == Mode.All || mode == Mode.OnlyConnection)
			packet.ChangeOrAddHeader("Connection", "Keep-Alive");
		
		if (mode == Mode.All || mode == Mode.OnlyUserAgent)
			packet.ChangeOrAddHeader("User-Agent", userAgent);
		
		return packet;
	}
	
	public String GetUserAgent () {
		return userAgent;
	}
	
	public void SetUserAgent (String userAgent) {
		this.userAgent = userAgent;
	}
	
	@Override
	public HTTPPacketFilter Clone() {
		return new HTTPBasicHeadersFilter (userAgent, mode, name);
	}
	
}
