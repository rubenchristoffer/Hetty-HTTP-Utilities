package rubenchristoffer.hetty.filters;

import java.net.URL;

import rubenchristoffer.hetty.HTTPPacket;

/**
 * Only filters request packets.
 * Updates 'Host' Header to reflect URL provided
 *
 */
public class HTTPHostFilter extends HTTPPacketFilter {

	public HTTPHostFilter () {
		super();
	}
	
	public HTTPHostFilter (String name) {
		super (name);
	}
	
	@Override
	public HTTPPacket Filter(HTTPPacket packet, URL url) {
		if (!packet.IsResponse())
			packet.ChangeOrInsertHeader("Host", url.getHost(), 0);
		
		return packet;
	}

	@Override
	public HTTPPacketFilter Clone() {
		return new HTTPHostFilter (name);
	}
	
}
