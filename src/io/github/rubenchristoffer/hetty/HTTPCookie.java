package io.github.rubenchristoffer.hetty;

import io.github.rubenchristoffer.hetty.validation.ArgumentValidator;

/**
 * Class for representing HTTP cookie.
 * @author Ruben Christoffer
 * @see <a href="https://tools.ietf.org/html/rfc6265">https://tools.ietf.org/html/rfc6265</a>
 */
public class HTTPCookie {
	
	protected String name;
	protected String value;
	protected HTTPCookieAttribute[] attributes;
	
	/**
	 * Creates a new HTTPCookie. 
	 * @param name is the name of the cookie
	 * @param value is the value of the cookie. Will be converted to empty string if null
	 * @param attributes is the attributes of the cookie. Can be empty array
	 * @throws IllegalArgumentException if name or attributes is null
	 */
	public HTTPCookie (String name, String value, HTTPCookieAttribute ... attributes) {
		this.name = ArgumentValidator.requireNonEmptyString(name, "name cannot be null or empty");
		this.value = ArgumentValidator.toEmptyStringIfNull(value);
		this.attributes = ArgumentValidator.requireNonNullArgument (attributes, "attributes cannot be null");
	}
	
	/**
	 * Gets the name.
	 * @return the name of the cookie
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Gets the value.
	 * @return the value of the cookie
	 */
	public String getValue () {
		return value;
	}
	
	/**
	 * Gets attributes.
	 * @return the HTTPCookieAttributes as array
	 */
	public HTTPCookieAttribute[] getAttributes () {
		return attributes;
	}
	
	/**
	 * Gets attribute based on name.
	 * @param name the name of the attribute
	 * @return the attribute if found, null otherwise
	 */
	public HTTPCookieAttribute getAttribute (String name) {
		ArgumentValidator.requireNonNullArgument(name, "name cannot be null");
		
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].getName().toLowerCase().equals(name.toLowerCase())) {
				return attributes[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Does the cookie contain this attribute?
	 * @param name the name of the attribute
	 * @return true if attribute was found, false otherwise
	 */
	public boolean containsAttribute (String name) {
		return getAttribute(name) != null;
	}
	
	/**
	 * Generates the cookie string based on name, value and potential attributes.
	 * This is the opposite of {@link io.github.rubenchristoffer.hetty.HTTPParser#parseCookie(String)}
	 * @return the string representation of this cookie
	 */
	public String generateCookie () {
		StringBuilder returnBuilder = new StringBuilder();
		returnBuilder.append(name + "=" + value);
		
		for (int i = 0; i < attributes.length; i++) {
			returnBuilder.append("; " + attributes[i].getName());
			
			if (attributes[i].getValue() != null)
				returnBuilder.append("=" + attributes[i].getValue());
		}
		
		return returnBuilder.toString();
	}
	
}
