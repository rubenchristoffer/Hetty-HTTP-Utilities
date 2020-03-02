package io.github.rubenchristoffer.hetty;

/**
 * Exception used when something goes wrong in HTTPNavigator.
 * @author Ruben Christoffer
 */
public class HTTPNavigatorException extends HTTPException {
	
	private static final long serialVersionUID = 3410635072393528231L;

	/**
	 * Creates a new HTTPNavigatorException.
	 * @param message is the message of the exception
	 * @param cause is the cause of the exception
	 */
	public HTTPNavigatorException (String message, Throwable cause) {
		super (message, cause);
	}
	
}
