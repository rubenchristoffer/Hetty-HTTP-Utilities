package rubenchristoffer.hetty.codec;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import rubenchristoffer.hetty.HTTPConstants;
import rubenchristoffer.hetty.HTTPPacket;

/**
 * HTTP Transfer Decoder that encodes body sent to server. 
 * NOTE: Does not support trailers!
 * @author Ruben Christoffer
 */
public class HTTPChunkedTransferEncoder extends HTTPTransferEncoder {
	
	private int[] preferredChunkSizes;
	
	/**
	 * Creates a new HTTPChunkedTransferEncoder.
	 * @param preferredChunkSizes is an array of lengths that represent the
	 * preferred size of each individual chunk
	 */
	public HTTPChunkedTransferEncoder (int ... preferredChunkSizes) {
		this.preferredChunkSizes = preferredChunkSizes;
	}
	
	@Override
	public boolean canEncode(HTTPPacket packet) {
		return packet.doesHeaderExist("Transfer-Encoding") && packet.getHeaders("Transfer-Encoding").get(0).equalsIgnoreCase("chunked");
	}
	
	@Override
	public ByteArrayOutputStream encode (byte[] body) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		int currentChunk = 0;
		int currentIndex = 0;
		int currentChunkSize;
		
		do {
			// Calculate next chunk size
			currentChunkSize = calculateNextChunkSize(currentChunk, currentIndex, body);
			
			// Append chunk size + CRLR
			byte[] chunkSizeBytes = Integer.toHexString(currentChunkSize).toUpperCase().getBytes(StandardCharsets.US_ASCII);
			out.write(chunkSizeBytes, 0, chunkSizeBytes.length);
			out.write(HTTPConstants.CRLF, 0, HTTPConstants.CRLF.length);
			
			// Append chunk + CRLR
			out.write(body, currentIndex, currentChunkSize);
			out.write(HTTPConstants.CRLF, 0, HTTPConstants.CRLF.length);
			
			// Update indexes
			currentIndex += currentChunkSize;
			currentChunk++;
		} while (currentChunkSize > 0);
		
		return out;
	}
	
	private int calculateNextChunkSize (int currentChunk, int currentIndex, byte[] body) {
		int bytesLeft = body.length - currentIndex;
		int preferredChunkSizeIndex = Math.min(currentChunk, preferredChunkSizes.length - 1);
		
		return bytesLeft >= preferredChunkSizes[preferredChunkSizeIndex] ? preferredChunkSizes[preferredChunkSizeIndex] : bytesLeft;
	}

}
