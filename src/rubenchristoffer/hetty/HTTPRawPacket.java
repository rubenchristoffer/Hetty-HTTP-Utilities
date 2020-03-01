package rubenchristoffer.hetty;

import java.io.ByteArrayOutputStream;

import rubenchristoffer.hetty.validation.ArgumentValidator;

/**
 * Raw HTTP packet.
 * This means that header part of packet and the body is split and is in byte form.
 * Header portion contains trailing CRLF as well.
 * @author Ruben Christoffer
 */
public class HTTPRawPacket {
	
	private ByteArrayOutputStream header;
	private ByteArrayOutputStream body;
	
	/**
	 * Creates a new HTTPRawPacket.
	 * @param header is the header stream containing header bytes for this packet
	 * @param body is the body stream containing body bytes for this packet
	 * @throws IllegalArgumentException if header or body is null
	 */
	public HTTPRawPacket (ByteArrayOutputStream header, ByteArrayOutputStream body) {
		this.header = ArgumentValidator.requireNonNullArgument (header, "header cannot be null");
		this.body = ArgumentValidator.requireNonNullArgument (body, "body cannot be null");
	}
	
	/**
	 * Gets header. 
	 * @return header stream
	 */
	public ByteArrayOutputStream getHeader() {
		return header;
	}

	/**
	 * Gets body.
	 * @return body stream
	 */
	public ByteArrayOutputStream getBody() {
		return body;
	}
	
	/**
	 * Combines header and body stream into a single byte array representing the entire packet.
	 * @return byte array representing packet
	 */
	public byte[] toByteArray () {
		ByteArrayOutputStream returnStream = new ByteArrayOutputStream(header.size() + body.size());
		
		returnStream.write(header.toByteArray(), 0, header.size());
		returnStream.write(body.toByteArray(), 0, body.size());
		
		return returnStream.toByteArray();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(header.size() + body.size());
		builder.append(new String(header.toByteArray(), HTTPConfig.HEADER_CHARSET));
		builder.append(new String(body.toByteArray(), HTTPConfig.STRING_PACKET_CHARSET));
		
		return builder.toString();
	}
	
}
