package rubenchristoffer.hetty;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a HTTP Cookie Jar.
 * This holds a list of validated cookies and allows to check
 * which cookies should be used when sending a request to a given URL.
 * @author Ruben Christoffer
 */
public class HTTPCookieJar {

	private ArrayList<HTTPValidatedCookie> cookieJar = new ArrayList<HTTPValidatedCookie>();
	
	/**
	 * Creates an empty HTTPCookieJar.
	 */
	public HTTPCookieJar () {}
	
	/**
	 * Gets the index of the first cookie by the given cookieName.
	 * @param cookieName is the name of the cookie
	 * @return index of the cookie if it is found, -1 otherwise
	 */
	public int indexOf (String cookieName) {
		for (int i = 0; i < cookieJar.size(); i++) {
			if (cookieJar.get(i).getName().equals(cookieName))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Checks if the cookie jar contains a cookie with the given cookieName.
	 * @param cookieName is the name of the cookie
	 * @return true if found in list, false otherwise
	 */
	public boolean contains (String cookieName) {
		return indexOf(cookieName) != -1;
	}
	
	/**
	 * Gets cookies with the given cookieName.
	 * @param cookieName is the name of the cookie(s)
	 * @return read-only wrapper list that is always up-to-date
	 */
	public List<HTTPValidatedCookie> getCookies (String cookieName) {
		ArrayList<HTTPValidatedCookie> returnList = new ArrayList<HTTPValidatedCookie>();
		
		for (int i = 0; i < cookieJar.size(); i++) {
			if (cookieJar.get(i).getName().equals(cookieName))
				returnList.add(cookieJar.get(i));
		}
		
		return Collections.unmodifiableList(returnList);
	}
	
	/**
	 * Sets / adds cookie based on name of cookie object.
	 * Replaces the first cookie with same name if it exists.
	 * @param cookie is the cookie object you want to add
	 */
	public void setCookie (HTTPValidatedCookie cookie) {
		int index = indexOf(cookie.getName());
		
		if (index == -1)
			cookieJar.add(cookie);
		else
			cookieJar.set(index, cookie);
	}
	
	/**
	 * Same as {@link #setCookie(HTTPValidatedCookie)},
	 * but performs this on multiple cookies instead of one.
	 * @param cookies the cookies you want to set
	 */
	public void setCookies (HTTPValidatedCookie ... cookies) {
		for (int i = 0; i < cookies.length; i++) {
			setCookie(cookies[i]);
		}
	}
	
	/**
	 * Gets cookies for a given URL.
	 * This is useful to filter out only the cookies that should be sent
	 * to a given host. 
	 * @param urlToValidate the URL you want cookies for
	 * @return an array of validated HTTP cookies
	 */
	public HTTPValidatedCookie[] getCookiesFor (URL urlToValidate) {
		ArrayList<HTTPValidatedCookie> returnList = new ArrayList<HTTPValidatedCookie>();
		
		for (int i = 0; i < cookieJar.size(); i++) {
			if (HTTPCookieValidator.validateURL(cookieJar.get(i), urlToValidate))
				returnList.add(cookieJar.get(i));
		}
		
		return returnList.toArray(new HTTPValidatedCookie[returnList.size()]);
	}
	
	/**
	 * Returns list of all cookies in jar.
	 * @return read-only wrapper list that is always up-to-date
	 */
	public List<HTTPValidatedCookie> getCookieJar () {
		return Collections.unmodifiableList(cookieJar);
	}
	
}
