package io.github.rubenchristoffer.hetty.codec;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP decoder responsible for decoding body when reading
 * packet from server.
 * @author Ruben Christoffer
 */
public abstract class HTTPTransferDecoder {
	
	/**
	 * Enum used for signalling the ability of this
	 * decoder to decode a packet.
	 * @author Ruben Christoffer
	 */
	public enum DecodeInfo {
		CAN_DECODE,
		CANNOT_DECODE,
		EMPTY_BODY
	}
	
	/**
	 * Gets decode info given packet header in form of Map.
	 * @param headers is all the packet header fields mapped as {@literal <header, value>}
	 * @return DecodeInfo enum
	 */
	public abstract DecodeInfo getDecodeInfo (Map<String, String> headers);
	
	/**
	 * All decoders should have the ability to be initialized based on packet header.
	 * This method does that.
	 * @param headers is all the packet header fields mapped as {@literal <header, value>}
	 */
	public abstract void initialize (HashMap<String, String> headers);
	
	/**
	 * Decodes the next byte read from server (or whatever source it might be).
	 * @param bodyStream is the current body in form of byte array output stream
	 * and this is where the next decoded byte will be written to
	 * @param nextByte is the next byte read from server (or whatever source it might be)
	 * @return true if reached end of packet, false if there is more content to be read
	 */
	public abstract boolean decodeNext(ByteArrayOutputStream bodyStream, int nextByte);
	
}
