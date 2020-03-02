package io.github.rubenchristoffer.hetty;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Utility class used for various HTTP parsing.
 * @author Ruben Christoffer
 */
public final class HTTPParser {

	/**
	 * Parses HTTP packet in form of byte array into a raw (unprocessed) HTTP packet.
	 * This means that it splits headers and the rest of the packet without
	 * doing any kind of processing.
	 * @param httpPacket is a whole HTTP packet in byte array form
	 * @return HTTPRawPacket object
	 */
	public static HTTPRawPacket parseRawPacket (byte[] httpPacket) {
		ByteArrayOutputStream headerStream = getHeaderStream(httpPacket);
		ByteArrayOutputStream bodyStream = getBodyStream(httpPacket, headerStream.size());
		
		return new HTTPRawPacket(headerStream, bodyStream);
	}
	
	/**
	 * Parses HTTP packet in form of byte array into HTTPPacket object.
	 * Same as {@link #parsePacket(HTTPRawPacket)} and {@link #parseRawPacket(byte[])}
	 * combined.
	 * @param httpPacket is a whole HTTP packet in byte array form
	 * @return HTTPPacket object
	 */
	public static HTTPPacket parsePacket (byte[] httpPacket) {
		return parsePacket(parseRawPacket(httpPacket));
	}
	
	/**
	 * Parses a raw HTTP packet into a HTTPPacket object.
	 * @param rawHttpPacket is the packet you want to parse
	 * @return HTTPPacket object
	 */
	public static HTTPPacket parsePacket (HTTPRawPacket rawHttpPacket) {
		HTTPPacket returnPacket;
		
		String header = new String(rawHttpPacket.getHeader().toByteArray(), HTTPConfig.HEADER_CHARSET);
		String[] headerLines = header.split("\n");
		headerLines[0] = headerLines[0].substring(0, headerLines[0].length() - 1);
		
		boolean response = headerLines[0].toLowerCase().startsWith("http");
		
		if (response) {
			HTTPResponse res = new HTTPResponse();
			
			if (headerLines[0].charAt(4) == '/')
				res.version = headerLines[0].split(" ")[0].split("/")[1];
			
			res.response = headerLines[0].split(" ", 2)[1];
			
			returnPacket = res;
		} else {
			HTTPRequest req = new HTTPRequest();
			String[] split = headerLines[0].split(" ");
			
			req.request = split[0];
			req.version = split[split.length - 1].split("/")[1];
			
			returnPacket = req;
		}
		
		for (int i = 1; i < headerLines.length - 1; i++) {
			returnPacket.addHeader(headerLines[i].substring(0, headerLines[i].length() - 1));
		}
		
		returnPacket.setBody(rawHttpPacket.getBody().toByteArray());
		
		return returnPacket;
	}
	
	/**
	 * Parses string into cookie. This is the opposite of
	 * {@link io.github.rubenchristoffer.hetty.HTTPCookie#generateCookie()}
	 * @param rawCookie is the string representation of cookie
	 * @return HTTPCookie object
	 */
	public static HTTPCookie parseCookie (String rawCookie) {
		String name = "";
		String value = "";
		ArrayList<HTTPCookieAttribute> attributes = new ArrayList<HTTPCookieAttribute>();
		
		String[] commaSplit = rawCookie.split(";", -1);
		
		for (int i = 0; i < commaSplit.length; i++) {
			String[] equalSplit = commaSplit[i].split("=", -1);
			
			if (equalSplit[0].startsWith(" "))
				equalSplit[0] = equalSplit[0].substring(1);
			
			if (i == 0) {
				name = equalSplit[0];
				value = equalSplit[1];
				
				for (int j = 2; j < equalSplit.length; j++) {
					value += "=" + equalSplit[j];
				}
				
				continue;
			} else {
				HTTPCookieAttribute attribute = null;
				
				if (equalSplit.length > 1) {
					String attributeValue = equalSplit[1];
					
					for (int j = 2; j < equalSplit.length; j++) {
						attributeValue += "=" + equalSplit[j];
					}
					
					attribute = new HTTPCookieAttribute(equalSplit[0], attributeValue);
				} else {
					attribute = new HTTPCookieAttribute(equalSplit[0]);
				}
				
				attributes.add(attribute);
			}
		}
		
		return new HTTPCookie (name, value, attributes.toArray(new HTTPCookieAttribute[attributes.size()]));
	}
	
	/**
	 * Gets a byte array stream containing the header of a HTTP packet + '\r\n' at the end.
	 * Calling .size() gives you the body offset,
	 * which is the same as the length of header + '\r\n'.
	 * @param rawHttpPacket is the raw http packet as byte array
	 * @return ByteArrayOutputStream containing bytes of header
	 */
	public static ByteArrayOutputStream getHeaderStream (byte[] rawHttpPacket) {
		ByteArrayOutputStream returnStream = new ByteArrayOutputStream();
		
		int progress = 0;
		
		for (int i = 0; i < rawHttpPacket.length; i++) {
			boolean carriageReturn = rawHttpPacket[i] == 13;
			boolean newLine = rawHttpPacket[i] == 10;
			
			returnStream.write(rawHttpPacket[i]);
			
			// return index + 1 when pattern '\r\n\r\n' is observed
			switch (progress) {
			case 0:
			case 2: if (carriageReturn) progress++; else progress = 0; break;
			case 1: if (newLine) progress++; else progress = 0; break;
			case 3: if (newLine) return returnStream;
			}
		}
		
		return returnStream;
	}
	
	/**
	 * Gets a byte array stream containing the body of a HTTP packet.
	 * This is often used in conjunction with {@link #getHeaderStream(byte[])}
	 * @param rawHttpPacket is the raw http packet as byte array
	 * @param bodyOffset is the offset into rawHttpPacket that marks the start of body
	 * @return ByteArrayOutputStream containing bytes of body
	 */
	public static ByteArrayOutputStream getBodyStream (byte[] rawHttpPacket, int bodyOffset) {
		ByteArrayOutputStream returnStream = new ByteArrayOutputStream();
		
		for (int i = bodyOffset; i < rawHttpPacket.length; i++) {
			returnStream.write(rawHttpPacket[i]);
		}
		
		return returnStream;
	}
	
	/**
	 * Makes '\n' and '\r' characters visible. Useful for debugging. 
	 * @param httpText is the text you want to unEscape.
	 * @return string where '\n' and '\r' are visible
	 */
	public static String unEscapeText (String httpText){
	    StringBuilder sb = new StringBuilder();
	    
	    for (int i = 0; i < httpText.length(); i++) {
	        switch (httpText.charAt(i)){
	            case HTTPConstants.ASCII_LF: sb.append("\\n"); break;
	            case HTTPConstants.ASCII_CR: sb.append("\\r"); break;
	            
	            default: sb.append(httpText.charAt(i));
	        }
	    }
	    
	    return sb.toString();
	}
	
}
