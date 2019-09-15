package rubenchristoffer.hetty;
import java.net.URL;
import java.util.ArrayList;

public class HTTPRequest extends HTTPPacket {
	
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
	
	public HTTPRequest () {
		responseRequest = "GET";
	}
	
	/**
	 * Sets HTTP request method.
	 * @param method
	 */
	public void SetRequestMethod (RequestMethod method) {
		this.responseRequest = method.name();
	}
	
	/**
	 * Sets HTTP request method.
	 * @param method
	 */
	public void SetRequestMethod (String method) {
		this.responseRequest = method;
	}
	
	/**
	 * Gets HTTP request method.
	 * @param method
	 */
	public String GetRequestMethod () {
		return responseRequest;
	}
	
	protected static String GeneratePacket (HTTPPacket packet, URL url) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(packet.responseRequest + " " + (url.getPath().equals("") ? "/" : url.getPath()) + (url.getQuery() == null ? "" : "?" + url.getQuery()) + " HTTP/" + packet.version + "\r\n");
		builder = HTTPPacket.AppendHeadersAndBody (packet, builder);
		
		return builder.toString();
	}

	@Override
	public HTTPCookie[] ParseCookies () {
		ArrayList<HTTPCookie> cookies = new ArrayList<HTTPCookie>();
		
		for (String header : GetHeaders("Cookie")) {
			String[] split = header.split(";", -1);
			
			for (int i = 0; i < split.length; i++) {
				cookies.add(HTTPCookie.Parse(split[i]));
			}
		}

		return cookies.toArray(new HTTPCookie[cookies.size()]);
	}
	
	public static String GenerateCookieHeader (HTTPCookie[] cookies) {
		StringBuilder returnBuilder = new StringBuilder();
		returnBuilder.append(cookies[0].GetName() + "=" + cookies[0].GetValue());
		
		for (int i = 1; i < cookies.length; i++) {
			returnBuilder.append("; " + cookies[i].GetName() + "=" + cookies[i].GetValue());
		}
		
		return returnBuilder.toString();
	}

	@Override
	public boolean IsResponse() {
		return false;
	}
	
}
