package io.github.rubenchristoffer.hetty;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import io.github.rubenchristoffer.hetty.HTTPRequest.RequestMethod;
import io.github.rubenchristoffer.hetty.validation.ArgumentValidator;

/**
 * HTTPNavigator is a class that builds on top of HTTPConnection (layer 2)
 * and is used for following redirects and will automatically handle
 * cookies for you between each request.
 * @author Ruben Christoffer
 */
public class HTTPNavigator {
	
	private HTTPConnection con;
	private HTTPRequest req;
	private HTTPResponse res;
	private HTTPCookieJar cookieJar;
	
	private int requestsSent = 0;
	private PrintStream debugOutputStream;
	
	/**
	 * Creates a new HTTPNavigator.
	 * @param con is the underlying connection (layer 1) the navigator should use
	 * @param req is the initial request that should be sent. 
	 * @throws IllegalArgumentException if con or req is null
	 */
	public HTTPNavigator (HTTPConnection con, HTTPRequest req) {
		this.con = ArgumentValidator.requireNonNullArgument (con, "con cannot be null");
		this.req = ArgumentValidator.requireNonNullArgument (req, "req cannot be null");
		this.cookieJar = new HTTPCookieJar();
	}
	
	/**
	 * Same as {@link #navigateNext(boolean)} where applyFilters=true
	 * @return Refer to {@link #navigateNext(boolean)}
	 */
	public boolean navigateNext () {
		return navigateNext (true);
	}
	
	/**
	 * Sends the current request and waits for a response.
	 * When response is received, it will update the new 'current' request
	 * using updated cookies if applicable. It will also always reset request method to GET.
	 * This can be called even if underlying HTTPConnection (layer 1) is closed, as it will attempt
	 * to open the connection if this is the case. 
	 * @param applyFilters determines whether filters should be applied when sending request
	 * @return true when there are no more redirects, false otherwise
	 */
	public boolean navigateNext (boolean applyFilters) {
		if (!con.isConnectionOpen())
			con.openConnection();
		
		if (debugOutputStream != null) {
			debugOutputStream.print(String.format("\n\n### HTTP NAVIGATOR REQUEST NR %d ###\n", requestsSent + 1)); 
			debugOutputStream.print(req.generatePacket(con.getURL(), applyFilters));
		}
		
		con.sendPacket(req, applyFilters);
		requestsSent++;
		
		res = (HTTPResponse) con.readPacket();
		
		if (debugOutputStream != null) {
			debugOutputStream.print(String.format("\n\n### HTTP NAVIGATOR RESPONSE NR %d ###\n", requestsSent)); 
			debugOutputStream.print(res.generatePacket(null));
		}
		
		List<String> locationHeaders = res.getHeaders("Location");
		
		if (res.getHeaders("Set-Cookie").size() > 0) {
			HTTPCookie[] cookies = res.getCookies();
			HTTPValidatedCookie[] validatedCookies = new HTTPValidatedCookie[cookies.length];
			
			for (int i = 0; i < validatedCookies.length; i++) {
				validatedCookies[i] = HTTPValidatedCookie.getValidatedCookie(cookies[i], con.getURL());
			}
			
			cookieJar.setCookies(validatedCookies);
		}
		
		if (locationHeaders.size() > 0) {
			URL newURL = null;
			
			try {
				newURL = new URL(locationHeaders.get(0));
			} catch (MalformedURLException e) {
				throw new HTTPNavigatorException ("Could not parse URL object from location header of response", e);
			}
			
			req.setRequestMethod(RequestMethod.GET);
			setURL (newURL);
			
			return true;
		}
	
		return false;
	}
	
	/**
	 * Calls {@link #navigateNext(boolean)} until there are no more redirects left.
	 * @param applyFilters determines whether filters should be applied when sending request
	 */
	public void navigateTillEnd (boolean applyFilters) {
		while (navigateNext(applyFilters)) {}
	}
	
	private void updateCookies () {
		HTTPValidatedCookie[] cookies = cookieJar.getCookiesFor(con.getURL());
		
		if (cookies.length > 0)
			req.changeOrAddHeader("Cookie", HTTPRequest.generateCookieHeader(cookies));
		else
			req.removeHeaderIfExists("Cookie");
	}
	
	/**
	 * Will call {@link io.github.rubenchristoffer.hetty.HTTPConnection#setURL(URL)} on underlying HTTPConnection,
	 * but also update the cookies of current request to match
	 * new URL. You should therefore use this method instead of setting the URL of the underlying HTTPConnection
	 * directly. 
	 * @param url is the new URL you want to set
	 */
	public void setURL (URL url) {
		con.setURL(url);
		updateCookies();
	}
	
	/**
	 * If you wish to enable debugging, you can set the printstream you want to print output to.
	 * Set this to null if you wish to disable debugging.
	 * By default debugging is set to false.
	 * @param debugOutputStream the outputstream you want to print debug messages to
	 */
	public void setDebugOutputStream (PrintStream debugOutputStream) {
		this.debugOutputStream = debugOutputStream;
	}
	
	/**
	 * Gets underlying HTTPConnection.
	 * @return the underlying HTTPConnection (layer 1)
	 */
	public HTTPConnection getConnection () {
		return con;
	}
	
	/**
	 * Gets the current request.
	 * @return the current request that should be sent next
	 */
	public HTTPRequest getCurrentRequest () {
		return req;
	}
	
	/**
	 * The last response received from server. 
	 * @return the last HTTPResponse received from server
	 */
	public HTTPResponse getLastResponse () {
		return res;
	}
	
	/**
	 * Gets the cookie jar used by the navigator to 
	 * keep track of cookies. 
	 * @return the HTTPCookieJar used by navigator
	 */
	public HTTPCookieJar getCookieJar() {
		return cookieJar;
	}
	
}
