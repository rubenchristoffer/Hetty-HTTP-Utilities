package rubenchristoffer.hetty;

public class HTTPCookieAttribute {

	private String name;
	private String value;
	
	public HTTPCookieAttribute (String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public HTTPCookieAttribute (String name) {
		this.name = name;
	}
	
	public String GetName () {
		return name;
	}
	
	public String GetValue () {
		return value;
	}
	
}
