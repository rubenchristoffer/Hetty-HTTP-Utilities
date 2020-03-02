package io.github.rubenchristoffer.hetty.validation;

/**
 * Utility class used for validating arguments
 * throughout the HTTP api.
 * @author Ruben Christoffer
 */
public final class ArgumentValidator {
	
	public static <T> T requireNonNullArgument (T obj, String message) {
		if (obj == null)
			throw new IllegalArgumentException(message);
		
		return obj;
	}
	
	public static <T> T[] requireNonEmptyArrayArgument (T[] obj, String message) {
		if (obj == null || obj.length == 0)
			throw new IllegalArgumentException(message);
		
		return obj;
	}
	
	public static byte[] requireNonEmptyByteArrayArgument (byte[] obj, String message) {
		if (obj == null || obj.length == 0)
			throw new IllegalArgumentException(message);
		
		return obj;
	}
	
	public static String toEmptyStringIfNull (String string) {
		if (string == null)
			return "";
		
		return string;
	}
	
	public static String requireNonEmptyString (String string, String message) {
		if (string == null || string.isEmpty())
			throw new IllegalArgumentException(message);
		
		return string;
	}
	
}
