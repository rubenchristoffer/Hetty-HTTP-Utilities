package rubenchristoffer.hetty.codec;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Transfer Decoder that decodes body sent from server. 
 * NOTE: Does not support trailers!
 * @author Ruben Christoffer
 */
public class HTTPChunkedTransferDecoder extends HTTPTransferDecoder {
	
	private Stage currentStage;
	private long currentChunkSize;
	
	private StringBuilder chunkSizeBuilder;
	private long currentChunkCounter;
	
	private enum Stage {
		NEW_CHUNK,
		READ_CHUNK,
		BODY_END,
		DONE
	}
	
	@Override
	public DecodeInfo getDecodeInfo(Map<String, String> headers) {
		if (headers.containsKey("transfer-encoding") && headers.get("transfer-encoding").equals("chunked")) {
			return DecodeInfo.CAN_DECODE;
		}
		
		return DecodeInfo.CANNOT_DECODE;
	}
	
	@Override
	public void initialize(HashMap<String, String> headers) {
		currentStage = Stage.NEW_CHUNK;
		currentChunkSize = -1;
		chunkSizeBuilder = new StringBuilder();
		currentChunkCounter = 0;
	}

	@Override
	public boolean decodeNext(ByteArrayOutputStream bodyStream, int byteRead) {
		switch (currentStage) {
		case NEW_CHUNK: runNewChunkStage(byteRead); break;
		case READ_CHUNK: runReadChunkStage(bodyStream, byteRead); break;
		case BODY_END: if (byteRead == 10) currentStage = Stage.DONE; break; // Clear the last CRLF at the end of stream
		default:
		}
		
		return currentStage == Stage.DONE;
	}
	
	private void runNewChunkStage(int byteRead) {
		switch (byteRead) {
		case 10: // At this point it has read "[CHUNKSIZE]\r\n" (in order words cleared CRLF). ASCII 10 stands for '\n'
			// Parse chunk size in hexadecimal form
			currentChunkSize = Long.parseLong(chunkSizeBuilder.toString(), 16);
			chunkSizeBuilder.setLength(0);
			
			// 0 indicates end of stream, so go to END stage if that is the case
			if (currentChunkSize != 0) {
				currentStage = Stage.READ_CHUNK;
			} else {
				currentStage = Stage.BODY_END;
			}
			
			break;
		case 13: break; // Every \r (ASCII 13) should be ignored to clear CRLF and should NOT be added to stream
		default: chunkSizeBuilder.append((char) byteRead);
		}
	}
	
	private void runReadChunkStage (ByteArrayOutputStream bodyStream, int byteRead) {
		if (currentChunkCounter == currentChunkSize) {
			// Clear CRLF after chunk
			if (byteRead == 10) {
				currentStage = Stage.NEW_CHUNK;
				currentChunkCounter = 0;
			}
		} else {
			// Write read byte to stream
			bodyStream.write(byteRead);
			currentChunkCounter++;
		}
	}
	
}