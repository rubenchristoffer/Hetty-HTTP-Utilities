package rubenchristoffer.hetty.codec;

import java.io.ByteArrayOutputStream;

import rubenchristoffer.hetty.HTTPPacket;

/**
 * HTTP encoder that can encode body when sending packet
 * @author Ruben Christoffer
 */
public abstract class HTTPTransferEncoder {
	
	/**
	 * Is this encoder suitable to encode packet?
	 * @param packet is the HTTPPacket you want to encode / send
	 * @return true if this encoder is suitable, false otherwise
	 */
	public abstract boolean canEncode (HTTPPacket packet);
	
	/**
	 * Encodes the entire body and writes it to a byte array output stream.
	 * @param body is the body you want to encode in form of a byte array
	 * @return ByteArrayOutputStream containing encoded body
	 */
	public abstract ByteArrayOutputStream encode (byte[] body);
	
}