package rubenchristoffer.hetty;

import rubenchristoffer.hetty.validation.ArgumentValidator;

/**
 * Data class representing HTTP Cookie Attribute.
 * @author Ruben Christoffer
 */
public class HTTPCookieAttribute {

	protected String name;
	protected String value;
	
	/**
	 * Creates a new HTTPCookieAttribute.
	 * @param name is the name of the attribute
	 * @param value is the value of the attribute. Will be converted to empty string if null
	 */
	public HTTPCookieAttribute (String name, String value) {
		this.name = name;
		this.value = ArgumentValidator.toEmptyStringIfNull (value);
	}
	
	/**
	 * Creates a new HTTPCookieAttribute where value is empty string.
	 * @param name is the name of the attribute
	 */
	public HTTPCookieAttribute (String name) {
		this (name, "");
	}
	
	/**
	 * Gets the name.
	 * @return the name of the attribute
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Gets the value
	 * @return the value of the attribute
	 */
	public String getValue () {
		return value;
	}
	
	/**
	 * Sets the value of the attribute
	 * @param value is the new value you want to set
	 */
	public void setValue (String value) {
		this.value = value;
	}
	
	@Override
	public String toString () {
		return name + "=" + value;
	}
	
}
