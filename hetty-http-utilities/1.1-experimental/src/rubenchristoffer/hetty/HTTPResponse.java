package rubenchristoffer.hetty;
import java.net.URL;
import java.util.ArrayList;

public class HTTPResponse extends HTTPPacket {
	
	public HTTPResponse () {}
	
	/**
	 * Sets HTTP status code and status text
	 * @param statusCode
	 * @param status
	 */
	public void SetStatus (int statusCode, String status) {
		this.responseRequest = statusCode + " " + status;
	}
	
	/**
	 * Gets HTTP status code
	 */
	public int GetStatusCode () {
		return Integer.parseInt(responseRequest.replaceAll("[^0-9]", "").trim());
	}
	
	/**
	 * Gets HTTP status text
	 * @return
	 */
	public String GetStatus () {
		return responseRequest.substring(4); // Status code is always 3 long + 1 space
	}

	protected static String GeneratePacket(HTTPPacket packet, URL url) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("HTTP/" + packet.version + " " + packet.responseRequest + "\r\n");
		builder = HTTPPacket.AppendHeadersAndBody (packet, builder);
		
		return builder.toString();
	}
	
	@Override
	public HTTPCookie[] ParseCookies() {
		ArrayList<HTTPCookie> cookies = new ArrayList<HTTPCookie>();
		
		for (String header : GetHeaders("Set-Cookie")) {
			cookies.add(HTTPCookie.Parse(header));
		}

		return cookies.toArray(new HTTPCookie[cookies.size()]);
	}
	
	@Override
	public boolean IsResponse() {
		return true;
	}
	
}
