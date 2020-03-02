package io.github.rubenchristoffer.hetty.codec;

import io.github.rubenchristoffer.hetty.HTTPException;

/**
 * Exception used when parts of the HTTP API encounters unsupported content.
 * Usually thrown if no suitable decoder is available for instance when reading packet
 * from server.
 * @author Ruben Christoffer
 */
public class HTTPUnsupportedContentException extends HTTPException {
	
	private static final long serialVersionUID = -1666174260027876062L;

	/**
	 * Creates a new HTTPUnsupportedContentException.
	 * @param message is the message of the exception
	 * @param cause is the cause of the exception
	 */
	public HTTPUnsupportedContentException (String message, Throwable cause) {
		super (message, cause);
	}
}
