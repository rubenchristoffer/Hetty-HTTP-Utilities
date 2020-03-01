package rubenchristoffer.hetty;

/**
 * Class that processes cookie attributes according to RFC6265 Section 5.2
 * @author Ruben Christoffer
 * @see <a href="https://tools.ietf.org/html/rfc6265#section-5.2.1">https://tools.ietf.org/html/rfc6265#section-5.2.1</a>
 */
public class HTTPCookieAttributeProcessor {
	
	/**
	 * Creates a new validated cookie attribute.
	 * @param name is the name of the cookie
	 * @param value is the value of the cookie
	 * @return a validated cookie attribute
	 */
	public HTTPValidatedCookieAttribute getValidatedAttribute (String name, String value) {
		String processedName = name.toLowerCase();
		String processedValue = value.toLowerCase();
		
		switch (processedName) {
		// Section 5.2.3
		case "domain":
			// Discard attribute if no domain value
			if (processedValue == null)
				return null;
			
			// Remove leading "." if it exists
			if (processedValue.startsWith("."))
				processedValue = processedValue.substring(1);
			
			break;
			
		// Section 5.2.4
		case "path":
			if (processedValue == null || !processedValue.startsWith("/")) {
				processedValue = "/";
			}
			
			break;
			
		// Section 5.2.5
		case "secure":
			processedName = "Secure";
			processedValue = null;
			break;
			
		// Section 5.2.6
		case "httponly":
			processedName = "HttpOnly";
			processedValue = null;
			break;
		}
		
		return new HTTPValidatedCookieAttribute(processedName, processedValue);
	}
	
	/**
	 * Same as {@link #getValidatedAttribute(String, String)},
	 * but uses a cookie attrubute object instead.
	 * @param attribute is the cookie attribute you want to validate
	 * @return a validated cookie attribute
	 */
	public HTTPValidatedCookieAttribute getValidatedAttribute (HTTPCookieAttribute attribute) {
		return getValidatedAttribute (attribute.getName (), attribute.getValue ());
	}
	
}