package rubenchristoffer.hetty;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class HTTPCookie {
	
	private String name;
	private String value;
	private HTTPCookieAttribute[] attributes;
	
	public HTTPCookie (String name, String value, HTTPCookieAttribute ... attributes) {
		Initialize(name, value, null, attributes);
	}
	
	public HTTPCookie (String name, String value, URL url, HTTPCookieAttribute ... attributes) {
		Initialize(name, value, url, attributes);
	}
	
	private void Initialize (String name, String value, URL url, HTTPCookieAttribute[] attributes) {
		this.name = name;
		this.value = value;
		
		ArrayList<HTTPCookieAttribute> temp = new ArrayList<HTTPCookieAttribute>();
		
		if (url != null) {
			boolean containsDomain = false;
			boolean containsPath = false;
			
			for (int i = 0; i < attributes.length; i++) {
				if (attributes[i].GetName().toLowerCase().equals("domain")) {
					containsDomain = true;
				} else if (attributes[i].GetName().toLowerCase().equals("path")) {
					containsPath = true;
				}
			}
			
			if (!containsDomain) {
				temp.add(new HTTPCookieAttribute ("domain", url.getHost()));
			}
			
			if (!containsPath) {
				temp.add(new HTTPCookieAttribute("path", url.getPath()));
			}
		}
		
		temp.addAll(Arrays.asList(attributes));
		
		this.attributes = temp.toArray(new HTTPCookieAttribute[temp.size()]);
	}
	
	public String GetName () {
		return name;
	}
	
	public String GetValue () {
		return value;
	}
	
	public HTTPCookieAttribute[] GetAttributes () {
		return attributes;
	}
	
	public HTTPCookieAttribute GetAttribute (String name) {
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].GetName().toLowerCase().equals(name.toLowerCase())) {
				return attributes[i];
			}
		}
		
		return null;
	}
	
	public boolean ContainsAttribute (String name) {
		return GetAttribute(name) != null;
	}
	
	public String GenerateCookie () {
		StringBuilder returnBuilder = new StringBuilder();
		returnBuilder.append(name + "=" + value);
		
		for (int i = 0; i < attributes.length; i++) {
			returnBuilder.append("; " + attributes[i].GetName());
			
			if (attributes[i].GetValue() != null)
				returnBuilder.append("=" + attributes[i].GetValue());
		}
		
		return returnBuilder.toString();
	}
	
	public static HTTPCookie Parse (String rawCookie) {
		String name = "";
		String value = "";
		ArrayList<HTTPCookieAttribute> attributes = new ArrayList<HTTPCookieAttribute>();
		
		String[] commaSplit = rawCookie.split(";", -1);
		
		for (int i = 0; i < commaSplit.length; i++) {
			String[] equalSplit = commaSplit[i].split("=", -1);
			
			if (equalSplit[0].startsWith(" "))
				equalSplit[0] = equalSplit[0].substring(1);
			
			if (i == 0) {
				name = equalSplit[0];
				value = equalSplit[1];
				
				for (int j = 2; j < equalSplit.length; j++) {
					value += "=" + equalSplit[j];
				}
				
				continue;
			} else {
				HTTPCookieAttribute attribute = null;
				
				if (equalSplit.length > 1) {
					String attributeValue = equalSplit[1];
					
					for (int j = 2; j < equalSplit.length; j++) {
						attributeValue += "=" + equalSplit[j];
					}
					
					attribute = new HTTPCookieAttribute(equalSplit[0], attributeValue);
				} else {
					attribute = new HTTPCookieAttribute(equalSplit[0]);
				}
				
				attributes.add(attribute);
			}
		}
		
		return new HTTPCookie (name, value, attributes.toArray(new HTTPCookieAttribute[attributes.size()]));
	}
	
}
