package rubenchristoffer.hetty.filters;

import java.net.URL;

import rubenchristoffer.hetty.HTTPPacket;

/**
 * Sets value of 'Connection' header to 'Keep-Alive'.
 * Sets value of 'User-Agent' header to userAgent provided. 
 * @author HallaBalla
 */
public class HTTPBasicHeadersFilter extends HTTPPacketFilter {
	
	private String userAgent;
	private Mode mode;
	
	/**
	 * Enum used for determining what headers this
	 * filter should affect.
	 * @author Ruben Christoffer
	 */
	public enum Mode {
		All,
		OnlyUserAgent,
		OnlyConnection
	}
	
	/**
	 * Creates a new HTTPBasicHeadersFilter.
	 * @param name is the name of the filter
	 * @param userAgent is the value of userAgent header you want to use
	 * @param mode determines which headers are affected by filter
	 */
	public HTTPBasicHeadersFilter (String name, String userAgent, Mode mode) {
		super (name);
		
		this.userAgent = userAgent;
		this.mode = mode;
	}
	
	@Override
	public void filter(HTTPPacket packet, URL url) {
		if (mode == Mode.All || mode == Mode.OnlyConnection)
			packet.changeOrAddHeader("Connection", "Keep-Alive");
		
		if (mode == Mode.All || mode == Mode.OnlyUserAgent)
			packet.changeOrAddHeader("User-Agent", userAgent);
	}
	
	/**
	 * Gets user agent.
	 * @return the user agent signature that is used for setting user-agent header
	 */
	public String getUserAgent () {
		return userAgent;
	}
	
	/**
	 * Sets user agent. 
	 * @param userAgent is the signature that is used when setting user-agent header
	 */
	public void setUserAgent (String userAgent) {
		this.userAgent = userAgent;
	}
	
	@Override
	public HTTPPacketFilter cloneDeep() {
		return new HTTPBasicHeadersFilter (name, userAgent, mode);
	}
	
}
