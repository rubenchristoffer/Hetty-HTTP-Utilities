package rubenchristoffer.hetty.filters;

import java.net.URL;

import rubenchristoffer.hetty.HTTPPacket;
import rubenchristoffer.hetty.misc.DeepCloneable;

/**
 * A HTTP filter modifies HTTP packet
 * (request or response). This can be very useful
 * for creating dynamic packets that automatically
 * update header fields or change packet body. 
 * @author Ruben Christoffer
 */
public abstract class HTTPPacketFilter implements DeepCloneable<HTTPPacketFilter> {
	
	protected String name;
	
	/**
	 * Creates a new HTTPPacketFilter.
	 * @param name is the name of the filter
	 */
	public HTTPPacketFilter (String name) {
		this.name = name;
	}
	
	/**
	 * Filters the packet. This may include adding / removing / modifying header fields
	 * or body. It may also do nothing if certain criteria is not fulfilled.
	 * @param packet is the packet you want to filter
	 * @param url is the URL you are sending the packet to
	 * @throws HTTPFilterException if something goes wrong filtering packet
	 */
	public abstract void filter (HTTPPacket packet, URL url);
	
	/**
	 * Gets name.
	 * @return the name of this filter
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Sets name.
	 * @param name is the new name of this filter
	 */
	public void setName (String name) {
		this.name = name;
	}
	
}