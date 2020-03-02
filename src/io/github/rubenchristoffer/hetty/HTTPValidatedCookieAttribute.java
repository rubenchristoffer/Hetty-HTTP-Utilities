package io.github.rubenchristoffer.hetty;

/**
 * Same as {@link io.github.rubenchristoffer.hetty.HTTPCookieAttribute},
 * but it is read-only and is properly validated. 
 * Will throw UnsupportedOperationException if you attempt to change
 * value.
 * @author Ruben Christoffer
 */
public class HTTPValidatedCookieAttribute extends HTTPCookieAttribute {

	protected HTTPValidatedCookieAttribute (String name, String value) {
		super (name, value);
	}
	
	@Override
	public void setValue (String value) {
		throw new UnsupportedOperationException ("It is not possible to set the value of a validated cookie attribute");
	}

}
