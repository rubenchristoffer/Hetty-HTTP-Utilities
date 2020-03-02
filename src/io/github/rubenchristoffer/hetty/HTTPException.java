package io.github.rubenchristoffer.hetty;

/**
 * Base class for all HTTP exceptions (unchecked exceptions).
 * You can use this if you wish to bundle up all
 * HTTP exceptions for some reason.
 * @author Ruben Christoffer
 */
public abstract class HTTPException extends RuntimeException {

	private static final long serialVersionUID = -8244760461581279610L;

	/**
	 * Creates a new HTTPException.
	 * @param message is the message of the exception.
	 * @param cause is the cause of the exception
	 */
	public HTTPException (String message, Throwable cause) { super (message, cause); }
	
}
