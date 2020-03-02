package io.github.rubenchristoffer.hetty.filters;

import java.net.URL;

import io.github.rubenchristoffer.hetty.HTTPPacket;

/**
 * Only filters request packets.
 * Updates 'Host' header to reflect URL provided.
 */
public class HTTPHostFilter extends HTTPPacketFilter {

	/**
	 * Creates a new HTTPHostFilter.
	 * @param name is the name of the filter
	 */
	public HTTPHostFilter (String name) { 
		super(name);
	}
	
	@Override
	public void filter(HTTPPacket packet, URL url) {
		if (!packet.isResponse())
			packet.changeOrInsertHeader("Host", url.getHost(), 0);
	}

	@Override
	public HTTPPacketFilter cloneDeep() {
		return new HTTPHostFilter (name);
	}
	
}
