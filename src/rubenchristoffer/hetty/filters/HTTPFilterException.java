package rubenchristoffer.hetty.filters;

import rubenchristoffer.hetty.HTTPException;

/**
 * Exception used when something goes wrong in a HTTPFilter.
 * @author Ruben Christoffer
 */
public class HTTPFilterException extends HTTPException {
	
	private static final long serialVersionUID = 3239331512671026145L;

	/**
	 * Creates a new HTTPFilterException.
	 * @param message is the message of the exception
	 * @param cause is the cause of the exception
	 */
	public HTTPFilterException (String message, Throwable cause) {
		super (message, cause);
	}
	
}
