package io.github.rubenchristoffer.hetty;

/**
 * Exception that occurs when dealing with a HTTP Connection.
 * @author Ruben Christoffer
 */
public class HTTPConnectionException extends HTTPException {
	
	private static final long serialVersionUID = -5337386847988189071L;

	/**
	 * Creates a new HTTPConnectionException.
	 * @param message is the exception message
	 * @param cause is the cause of the exception
	 */
	public HTTPConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

}
