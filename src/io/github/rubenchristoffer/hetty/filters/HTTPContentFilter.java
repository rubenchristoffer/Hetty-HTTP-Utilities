package io.github.rubenchristoffer.hetty.filters;

import java.net.URL;
import java.nio.charset.Charset;

import io.github.rubenchristoffer.hetty.HTTPConfig;
import io.github.rubenchristoffer.hetty.HTTPPacket;

/**
 * Sets 'Content-Length' header to number of bytes in packet body,
 * unless 'Transfer-Encoding' header exists.
 * Uses system default charset by default. 
 * @author HallaBalla
 */
public class HTTPContentFilter extends HTTPPacketFilter {
	
	private Charset sourceCharset;
	private Charset targetCharset;
	
	/**
	 * Creates a new HTTPContentFilter.
	 * @param name is the name of the filter
	 * @param sourceCharset is the charset for the source content
	 * @param targetCharset is the charset that is used for converting content
	 */
	public HTTPContentFilter (String name, Charset sourceCharset, Charset targetCharset) {
		super(name);
		
		this.sourceCharset = sourceCharset;
		this.targetCharset = targetCharset;
	}
	
	/**
	 * Creates a new HTTPContentFilter where sourceCharset=system default
	 * and targetCharset={@link io.github.rubenchristoffer.hetty.HTTPConfig#STRING_PACKET_CHARSET}
	 * @param name is the name of the filter
	 * @see #HTTPContentFilter(String, Charset, Charset)
	 */
	public HTTPContentFilter (String name) {
		this(name, Charset.defaultCharset (), HTTPConfig.STRING_PACKET_CHARSET);
	}
	
	@Override
	public void filter (HTTPPacket packet, URL url) {
		packet.setBody(new String(packet.getBody(), sourceCharset).getBytes(targetCharset));
		
		if (!packet.doesHeaderExist("Transfer-Encoding", "Chunked")) {
			if (packet.getBodyLength() > 0) {
				packet.changeOrAddHeader("Content-Length", packet.getBodyLength() + "");
			} else {
				packet.changeOrAddHeader("Content-Length", "0");
			}
		}
	}
	
	/**
	 * Gets the source charset.
	 * @return source charset used for 'understanding' source
	 */
	public Charset getSourceCharset () {
		return sourceCharset;
	}
	
	/**
	 * Gets the target charset.
	 * @return target charset used for encoding content
	 */
	public Charset getTargetCharset () {
		return targetCharset;
	}
	
	@Override
	public HTTPPacketFilter cloneDeep() {
		return new HTTPContentFilter (name, sourceCharset, targetCharset);
	}
	
}
