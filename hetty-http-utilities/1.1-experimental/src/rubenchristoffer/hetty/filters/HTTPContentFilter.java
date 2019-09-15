package rubenchristoffer.hetty.filters;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import rubenchristoffer.hetty.HTTPPacket;

/**
 * Sets 'Content-Length' header to number of bytes in packet body,
 * unless 'Transfer-Encoding' header exists.
 * Uses UTF-8 encoding by default. 
 * @author HallaBalla
 *
 */
public class HTTPContentFilter extends HTTPPacketFilter {
	
	private Charset charset = StandardCharsets.UTF_8;
	
	public HTTPContentFilter () {
		super();
	}
	
	public HTTPContentFilter (Charset charset) {
		super();
		
		this.charset = charset;
	}
	
	public HTTPContentFilter (String name) {
		super(name);
	}
	
	public HTTPContentFilter (Charset charset, String name) {
		super(name);
		
		this.charset = charset;
	}
	
	@Override
	public HTTPPacket Filter (HTTPPacket packet, URL url) {
		if (!packet.DoesHeaderExist("Transfer-Encoding", "Chunked")) {
			if (packet.GetBody().length() > 0) {
				packet.ChangeOrAddHeader("Content-Length", packet.GetBody().getBytes(charset).length + "");
			} else {
				packet.ChangeOrAddHeader("Content-Length", "0");
			}
		}
		
		return packet;
	}
	
	public Charset GetCharset () {
		return charset;
	}
	
	public void SetCharset (Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public HTTPPacketFilter Clone() {
		return new HTTPContentFilter (charset, name);
	}
	
}
