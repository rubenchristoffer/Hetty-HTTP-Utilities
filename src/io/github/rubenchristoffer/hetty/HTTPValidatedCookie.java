package io.github.rubenchristoffer.hetty;

import java.net.URL;
import java.util.ArrayList;

/**
 * A Validated HTTP Cookie has validated attributes.
 * @author HallaBalla
 */
public class HTTPValidatedCookie extends HTTPCookie {
	
	private HTTPValidatedCookie(String name, String value, HTTPCookieAttribute[] attributes) {
		super(name, value, attributes);
	}
	
	/**
	 * Creates a validated cookie based on HTTPCookie for a given URL.
	 * @param cookie is the cookie you want to get a validated version of
	 * @param url is the URL you want to validate for
	 * @return HTTPValidatedCookie object
	 */
	public static HTTPValidatedCookie getValidatedCookie (HTTPCookie cookie, URL url) {
		ArrayList<HTTPValidatedCookieAttribute> processedCookieAttributes = new ArrayList<HTTPValidatedCookieAttribute>(cookie.attributes.length);
		boolean containsDomain = false;
		boolean containsPath = false;
		
		for (int i = 0; i < cookie.attributes.length; i++) {
			HTTPValidatedCookieAttribute processedAttribute = HTTPConfig.COOKIE_ATTRIBUTE_PROCESSOR.getValidatedAttribute(cookie.attributes[i]);
			
			if (processedAttribute != null) {
				processedCookieAttributes.add(processedAttribute);
				
				if (processedAttribute.getName().equals("domain"))
					containsDomain = true;
				if (processedAttribute.getName().equals("path"))
					containsPath = true;
			}
		}
		
		if (!containsDomain)
			processedCookieAttributes.add(HTTPConfig.COOKIE_ATTRIBUTE_PROCESSOR.getValidatedAttribute ("domain", url.getHost().toLowerCase()));
		
		if (!containsPath)
			processedCookieAttributes.add(HTTPConfig.COOKIE_ATTRIBUTE_PROCESSOR.getValidatedAttribute ("path", url.getPath().toLowerCase()));
		
		return new HTTPValidatedCookie(cookie.name, cookie.value, processedCookieAttributes.toArray(new HTTPValidatedCookieAttribute[processedCookieAttributes.size()]));
	}
	
}