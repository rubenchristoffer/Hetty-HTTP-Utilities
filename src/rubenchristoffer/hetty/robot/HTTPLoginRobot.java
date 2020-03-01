package rubenchristoffer.hetty.robot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import rubenchristoffer.hetty.HTTPConfig;
import rubenchristoffer.hetty.HTTPConnection;
import rubenchristoffer.hetty.HTTPNavigator;
import rubenchristoffer.hetty.HTTPRequest;
import rubenchristoffer.hetty.HTTPRequest.RequestMethod;
import rubenchristoffer.hetty.filters.HTTPFormDataFilter;
import rubenchristoffer.hetty.html.HTMLDocumentWrapper;
import rubenchristoffer.hetty.html.HTMLNodeWrapper;
import rubenchristoffer.hetty.html.HTMLParser;
import rubenchristoffer.hetty.html.HTMLPredicates;
import rubenchristoffer.hetty.validation.ArgumentValidator;

/**
 * HTTPLoginRobot is a class that is specifially designed
 * to navigate to a login page, fill in credentials and post
 * the form and log the user in.
 * It supports automatic detection of correct username / email and password
 * nodes when setting credentials. All HTML parsing is handled
 * by a given HTMLParser.
 * This class is built on top of HTTPNavigator and is therefore layer 3.
 * @author Ruben Christoffer
 */
public class HTTPLoginRobot {
	
	/**
	 * Array of keywords that are used during automatic username / email
	 * node detection. It takes into account the order of words.
	 */
	public static String[] USERNAME_KEYWORDS = new String[] {
		"username",
		"mail",
		"name",
		"user"
	};
	
	/**
	 * Array of keywords that are used during automatic password
	 * node detection. It takes into account the order of words.
	 */
	public static String[] PASSWORD_KEYWORDS = new String[] {
		"password",
		"pass"
	};
	
	private HTTPNavigator navigator;
	private HTMLParser htmlParser;
	
	private HTMLDocumentWrapper html;
	private HashMap<String, String> formData = new HashMap<String, String>();
	
	/**
	 * Creates a new HTTPLoginRobot using given htmlParser.
	 * @param htmlParser is a parser that will turn a HTML string into a DOM document
	 */
	public HTTPLoginRobot (HTMLParser htmlParser) {
		HTTPRequest req = new HTTPRequest(true);
		req.insertFilter(new HTTPFormDataFilter("form_data", formData), 0);
		
		this.navigator = new HTTPNavigator(new HTTPConnection(), req);
		this.htmlParser = htmlParser;
	}
	
	/**
	 * Navigates to the login page using HTTPNavigator.
	 * That means it will follow redirects. It will parse HTML
	 * when there are no more redirects.
	 * @param url is the URL of the login page
	 */
	public void navigateToLoginPage (URL url) {
		navigator.setURL(url);
		navigator.navigateTillEnd(true);
		parseHTMLFromResponse();
	}
	
	/**
	 * Gets the current form data in form of a {@literal <name, value>} map.
	 * @return read-only wrapper map of form data
	 */
	public Map<String, String> getFormData () {
		return Collections.unmodifiableMap(formData);
	}
	
	/**
	 * Updates form data from last parsed HTML.
	 * @throws HTTPRobotException if URL provided by 'action' attribute in form
	 * is not a valid URL
	 */
	public void updateFormData () {
		formData.clear();
		
		HTMLNodeWrapper form = html.getNodeListWrapperByName("form", 
				HTMLPredicates.HAS_ATTRIBUTE_VALUE("method", "post")).first();
		
		String actionURL = form.getSafeAttributeValue("action");
		
		if (!actionURL.isEmpty()) {
			try {
				navigator.setURL(new URL(actionURL));
			} catch (MalformedURLException e) {
				throw new HTTPRobotException("URL provided by 'action' attribute in form is not a valid URL", e);
			}
		}
		
		for (HTMLNodeWrapper input : form.getNodeListWrapperByName("input")) {
			String name = input.getNameAttribute();
			String value = input.getValueAttribute();
			
			if (name != null) {
				formData.put(name, value != null ? value : "");
			}
		}
	}
	
	/**
	 * Updates form data with login credentials.
	 * @param username is the username / email used to login
	 * @param password is the password used to login
	 * @throws HTTPRobotException if auto-detection of usernameNode or passwordNode fails
	 */
	public void setLoginCredentials (String username, String password) {
		try {
			setLoginCredentials (autoDetectUsernameNodeName(), autoDetectPasswordNodeName(), username, password);
		} catch (IllegalArgumentException e) {
			throw new HTTPRobotException("Could not auto-detect usernameNodeName or passwordNodeName", e);
		}
	}
	
	/**
	 * Updates form data with login credentials.
	 * @param usernameNodeName is the name of the HTML username input node
	 * @param passwordNodeName is the name of the HTML password input node
	 * @param username is the username / email used to login
	 * @param password is the password used to login
	 * @throws IllegalArgumentException if usernameNodeName or passwordNodeName is null or empty
	 * @throws NoSuchElementException if no node matches the usernameNodeName or passwordNodeName
	 */
	public void setLoginCredentials (String usernameNodeName, String passwordNodeName, String username, String password) {
		ArgumentValidator.requireNonEmptyString (usernameNodeName, "Username node name cannot be null or empty");
		ArgumentValidator.requireNonEmptyString (passwordNodeName, "Password node name cannot be null or empty");
		
		if (!formData.containsKey(usernameNodeName))
			throw new NoSuchElementException("There is no form data that matches the usernameNodeName '" + usernameNodeName + "'.");
		if (!formData.containsKey(passwordNodeName))
			throw new NoSuchElementException("There is no form data that matches the passwordNodeName '" + passwordNodeName + "'.");
	
		formData.put(usernameNodeName, username);
		formData.put(passwordNodeName, password);
	}
	
	/**
	 * Sends the form with the current form data
	 * and follows all redirects. It will parse HTML
	 * when there are no more redirects.
	 */
	public void sendForm () {
		navigator.getCurrentRequest().setRequestMethod(RequestMethod.POST);
		navigator.navigateTillEnd(true);
		parseHTMLFromResponse();
	}
	
	/**
	 * Method for auto-detecting a node name given an array of keywords (in order).
	 * @param keywords is an array of keywords the node name can contain (ignoring case)
	 * @return name of node if found, null otherwise
	 */
	public String autoDetectNodeName (String[] keywords) {
		for (String keyword : keywords) {
			String username = containsFormNodeNameIgnoreCase (keyword);
			
			if (username != null) { 
				return username;
			}
		}
		
		return null;
	}
	
	/**
	 * Method for auto-detecting username / email node based on
	 * {@link #USERNAME_KEYWORDS}
	 * @return name of username node if found, null otherwise
	 * @see #autoDetectNodeName(String[])
	 */
	public String autoDetectUsernameNodeName () {
		return autoDetectNodeName(USERNAME_KEYWORDS);
	}
	
	/**
	 * Method for auto-detecting password node based on
	 * {@link #PASSWORD_KEYWORDS}
	 * @return name of password node if found, null otherwise
	 * @see #autoDetectNodeName(String[])
	 */
	public String autoDetectPasswordNodeName () {
		return autoDetectNodeName(PASSWORD_KEYWORDS);
	}
	
	/**
	 * Gets the underlying HTTPNavigator used (layer 2)
	 * @return HTTPNavigator used by login robot
	 */
	public HTTPNavigator getNavigator () {
		return navigator;
	}
	
	private void parseHTMLFromResponse () {
		if (navigator.getLastResponse().getBodyLength() > 0) {
			html = htmlParser.parseAndWrapDocument(new String(navigator.getLastResponse().getBody(), HTTPConfig.STRING_PACKET_CHARSET));
		}
	}
	
	private String containsFormNodeNameIgnoreCase (String name) {
		for (String key : formData.keySet()) {
			if (key.toLowerCase().contains(name.toLowerCase()))
				return key;
		}
		
		return null;
	}

}
