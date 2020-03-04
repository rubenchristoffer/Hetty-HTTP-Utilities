package io.github.rubenchristoffer.hetty;

import java.net.URL;

/**
 * Class used for validation if HTTPValidatedCookie is eligable to be sent
 * to a given URL based on various criteria. 
 * @author Ruben Christoffer
 * @see <a href="https://tools.ietf.org/html/rfc6265">https://tools.ietf.org/html/rfc6265</a>
 */
public class HTTPCookieValidator {
	
	/**
	 * Validates entire URL, which includes its domain and path.
	 * @param validatedCookie is the (processed) cookie that client has stored or wants to check for
	 * @param urlToValidate The URL to validate
	 * @return true if URL checks out, false if it doesn't or if cookie does not contain domain or path attribute
	 */
	public static boolean validateURL (HTTPValidatedCookie validatedCookie, URL urlToValidate) {
		if (!validateDomain(validatedCookie, urlToValidate.getHost()))
			return false;
		
		if (!validatePath(validatedCookie, urlToValidate.getPath()))
			return false;
		
		return true;
	}
	
	/**
	 * Validates domain to see if it is allowed to handle cookie.
	 * @param validatedCookie is the (processed) cookie that client has stored or wants to check for
	 * @param domainToValidate is the domain to validate
	 * @return true if domain checks out, false if it doesn't or if cookie does not contain domain attribute
	 * @see <a href="https://tools.ietf.org/html/rfc6265#section-5.1.3">https://tools.ietf.org/html/rfc6265#section-5.1.3</a>
	 */
	public static boolean validateDomain (HTTPValidatedCookie validatedCookie, String domainToValidate) {
		String string = domainToValidate.toLowerCase();
		String domainString = "";
		
		if (validatedCookie.containsAttribute("domain")) {
			// No need for lower-case as it is a processed attribute
			domainString = validatedCookie.getAttribute("domain").getValue();
		} else {
			return false;
		}
		
		// Point 1
		if (domainString.equals(string))
			return true;
		
		// Point 2
		// Point 2 Requirement 1
		if (!string.endsWith(domainString))
			return false;
		
		// Point 2 Requirement 2
		if (!string.substring(0, string.length() - domainString.length()).endsWith("."))
			return false;
		
		// Point 2 Requirement 3
		if (string.matches("#.#.#.#"))
			return false;
		
		return true;
	}
	
	/**
	 * Validates path to see if it is allowed to handle cookie.
	 * @param validatedCookie The (validated) cookie that client has stored or wants to check for
	 * @param pathToValidate is the path you want to validate for
	 * @return true if path checks out, false if it doesn't or if cookie does not contain path attribute
	 * @see <a href="https://tools.ietf.org/html/rfc6265#section-5.1.4">https://tools.ietf.org/html/rfc6265#section-5.1.4</a>
	 */
	public static boolean validatePath (HTTPValidatedCookie validatedCookie, String pathToValidate) {
		String cookiePath = "";
		
		if (validatedCookie.containsAttribute("path")) {
			cookiePath = validatedCookie.getAttribute("path").getValue();
		} else {
			return false;
		}
		
		// Point 2, 3
		if (pathToValidate.startsWith(cookiePath)) {
			// Point 2
			if (cookiePath.endsWith("/"))
				return true;
			
			// Point 3
			if (pathToValidate.substring(cookiePath.length()).startsWith("/"))
				return true;
		}
		
		// Point 1
		return cookiePath.equals(pathToValidate);
	}
	
}