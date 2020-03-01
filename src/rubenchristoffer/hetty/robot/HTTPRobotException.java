package rubenchristoffer.hetty.robot;

import rubenchristoffer.hetty.HTTPException;

/**
 * Exception that is used when something goes wrong in HTTPLoginRobot class.
 * @author Ruben Christoffer
 */
public class HTTPRobotException extends HTTPException {

	private static final long serialVersionUID = 8474864930518111615L;

	/**
	 * Creates a new HTTPRobotException.
	 * @param message is the message of the exception
	 * @param cause is the cause of the exception
	 */
	public HTTPRobotException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
