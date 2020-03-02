package io.github.rubenchristoffer.hetty;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Class containing global variables used throughout the API.
 * CAUTION: It is possible to change these values, but be advised
 * that you may cause problems with certain parts of the API
 * relying on the default values.
 * Only change these values if there are no other alternatives possible.
 * @author Ruben Christoffer
 */
public final class HTTPConfig {

	public static Charset HEADER_CHARSET = StandardCharsets.US_ASCII;
	public static Charset STRING_PACKET_CHARSET = StandardCharsets.UTF_8;
	public static String FORM_DATA_CHARSET = "UTF-8";
	public static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0";
	public static HTTPCookieAttributeProcessor COOKIE_ATTRIBUTE_PROCESSOR = new HTTPCookieAttributeProcessor();
	
}