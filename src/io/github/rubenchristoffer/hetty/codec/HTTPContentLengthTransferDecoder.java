package io.github.rubenchristoffer.hetty.codec;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the default decoder used when decoding
 * HTTP packets. It is based on the content-length
 * header.
 * @author Ruben Christoffer
 */
public class HTTPContentLengthTransferDecoder extends HTTPTransferDecoder {
	
	private int contentLength = -1;
	private int contentCount = 0;

	@Override
	public DecodeInfo getDecodeInfo(Map<String, String> headers) {
		if (headers.containsKey("content-length")) {
			int contentLength = Integer.parseInt(headers.get("content-length"));
			
			if (contentLength != 0)
				return DecodeInfo.CAN_DECODE;
			else
				return DecodeInfo.EMPTY_BODY;
		} else {
			return DecodeInfo.CANNOT_DECODE;
		}
	}
	
	@Override
	public void initialize(HashMap<String, String> headers) {
		contentLength = Integer.parseInt(headers.get("content-length"));
		contentCount = 0;
	}
		
	@Override
	public boolean decodeNext(ByteArrayOutputStream bodyStream, int byteRead) {
		bodyStream.write(byteRead);
		contentCount++;
		
		if (contentCount == contentLength)
			return true;
		
		return false;
	}

}
