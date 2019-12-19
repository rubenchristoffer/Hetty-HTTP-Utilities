package rubenchristoffer.hetty;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

import rubenchristoffer.hetty.filters.HTTPBasicHeadersFilter;
import rubenchristoffer.hetty.filters.HTTPBasicHeadersFilter.Mode;
import rubenchristoffer.hetty.validation.ArgumentValidator;
import rubenchristoffer.hetty.filters.HTTPContentFilter;
import rubenchristoffer.hetty.filters.HTTPHostFilter;

/**
 * HTTP Packet in form of a request.
 * @author Ruben Christoffer
 * @see rubenchristoffer.hetty.HTTPPacket
 */
public class HTTPRequest extends HTTPPacket {
	
	protected String request;
	
	/**
	 * Enum containing the most known HTTP request methods
	 * @author Ruben Christoffer
	 */
	public enum RequestMethod {
		GET,
		HEAD,
		POST,
		PUT,
		DELETE,
		CONNECT,
		OPTIONS,
		TRACE,
		PATCH
	}
	
	/**
	 * Creates a new HTTPRequest where addDefaultFilters=true.
	 */
	public HTTPRequest () {
		initialize(true);
	}
	
	/**
	 * Creats a new HTTPRequest. 
	 * @param addDefaultFilters determines whether the default filters 
	 * (HostFilter, BasicHeadersFilter and ContentFilter) should be added to packet
	 */
	public HTTPRequest (boolean addDefaultFilters) {
		initialize(addDefaultFilters);
	}
	
	private void initialize (boolean addDefaultFilters) {
		this.request = "GET";
		
		if (addDefaultFilters) {
			addFilter(new HTTPHostFilter("host"));
			addFilter(new HTTPBasicHeadersFilter("basic_headers", HTTPConfig.DEFAULT_USER_AGENT, Mode.All));
			addFilter(new HTTPContentFilter("content"));
		}
	}
	
	/**
	 * Sets HTTP request method.
	 * @param method is the new request method
	 * @throws IllegalArgumentException if method is null
	 */
	public void setRequestMethod (RequestMethod method) {
		ArgumentValidator.requireNonNullArgument (method, "method cannot be null");
		
		this.request = method.name();
	}
	
	/**
	 * Sets HTTP request method.
	 * @param method is the new request method
	 */
	public void setRequestMethod (String method) {
		this.request = method;
	}
	
	/**
	 * Gets HTTP request method.
	 * @return current request method
	 */
	public String getRequestMethod () {
		return request;
	}
	
	@Override
	public HTTPRawPacket generatePacket(URL url, boolean applyFilters) {
		ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
		ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
		HTTPPacket packet = applyFilters ? filterAll(this, url) : this;
		
		appendHeaderText(headerStream, ((HTTPRequest)packet).request + " " + (url.getPath().equals("") ? "/" : url.getPath()) + (url.getQuery() == null ? "" : "?" + url.getQuery()) + " HTTP/" + packet.version + "\r\n");
		appendHeaders(headerStream, packet);
		appendBytes(bodyStream, packet.getBody());
		
		return new HTTPRawPacket(headerStream, bodyStream);
	}

	@Override
	public HTTPCookie[] getCookies () {
		ArrayList<HTTPCookie> cookies = new ArrayList<HTTPCookie>();
		
		for (String header : getHeaders("Cookie")) {
			String[] split = header.split(";", -1);
			
			for (int i = 0; i < split.length; i++) {
				cookies.add(HTTPParser.parseCookie(split[i]));
			}
		}

		return cookies.toArray(new HTTPCookie[cookies.size()]);
	}

	@Override
	public boolean isResponse() {
		return false;
	}
	
	/**
	 * Provides useful information about HTTP Request. 
	 */
	@Override
	public String toString () {
		String headerString = "";
		
		for (String header : headersList)
			headerString += "'" + header + "'\n";
		
		return String.format("### HTTP REQUEST INFO ###\nRequest: %s, Version: %s\nHeaders: \n%s\nBody:\n'%s'\n### END OF HTTP REQUEST INFO ###", request, version, headerString, body);
	}

	@Override
	public HTTPPacket cloneDeep() {
		HTTPRequest returnPacket = new HTTPRequest();
		
		returnPacket.request = request;
		returnPacket.version = version;
		
		for (int i = 0; i < headersList.size(); i++) {
			returnPacket.headersList.add(headersList.get(i));
		}
		
		returnPacket.body = new ByteArrayOutputStream();
		returnPacket.body.write(getBody(), 0, getBodyLength());
		
		for (int i = 0; i < filterPipeline.size(); i++) {
			returnPacket.filterPipeline.add(filterPipeline.get(i).cloneDeep());
		}
		
		return returnPacket;
	}
	
	/**
	 * Generates full cookie header used in cookie header
	 * for HTTP requests.
	 * @param cookies the cookies you want to generate string for
	 * @return string representing multiple cookies for HTTP request
	 */
	public static String generateCookieHeader (HTTPCookie ... cookies) {
		StringBuilder returnBuilder = new StringBuilder();
		
		if (cookies.length > 0)
			returnBuilder.append(cookies[0].getName() + "=" + cookies[0].getValue());
		
		for (int i = 1; i < cookies.length; i++) {
			returnBuilder.append("; " + cookies[i].getName() + "=" + cookies[i].getValue());
		}
		
		return returnBuilder.toString();
	}
	
}
