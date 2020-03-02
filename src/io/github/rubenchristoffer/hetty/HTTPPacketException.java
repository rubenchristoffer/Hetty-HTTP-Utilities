package io.github.rubenchristoffer.hetty;

/**
 * Exception used when something goes wrong within an HTTPPacket object.
 * @author Ruben Christoffer
 */
public class HTTPPacketException extends HTTPException {

	private static final long serialVersionUID = 3166418194536140827L;
	
	/**
	 * Creates a new HTTPPacketException.
	 * @param message is the message of the exception
	 * @param cause is the cause of the exception
	 */
	public HTTPPacketException(String message, Throwable cause) {
		super(message, cause);
	}

}
