package io.github.rubenchristoffer.hetty;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * HTTP Packet in form of a response.
 * @author Ruben Christoffer
 * @see io.github.rubenchristoffer.hetty.HTTPPacket
 */
public class HTTPResponse extends HTTPPacket {
	
	protected String response;
	
	/**
	 * Creates a new HTTPResponse.
	 */
	public HTTPResponse () {}
	
	/**
	 * Sets HTTP status code and status text
	 * @param statusCode is the new status code
	 * @param status is the new status description
	 */
	public void setStatus (int statusCode, String status) {
		this.response = statusCode + " " + status;
	}
	
	/**
	 * Gets HTTP status code.
	 * @return status code as int
	 */
	public int getStatusCode () {
		return Integer.parseInt(response.replaceAll("[^0-9]", "").trim());
	}
	
	/**
	 * Gets HTTP status text / description.
	 * @return string description of status
	 */
	public String getStatus () {
		return response.substring(4); // Status code is always 3 long + 1 space
	}

	@Override
	public HTTPRawPacket generatePacket(URL url, boolean applyFilters) {
		ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
		ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
		HTTPPacket packet = applyFilters ? filterAll(this, url) : this;
		
		appendHeaderText(headerStream, "HTTP/" + packet.version + " " + ((HTTPResponse)packet).response + "\r\n");
		appendHeaders(headerStream, packet);
		appendBytes(bodyStream, getBody());
		
		return new HTTPRawPacket(headerStream, bodyStream);
	}
	
	@Override
	public HTTPCookie[] getCookies() {
		ArrayList<HTTPCookie> cookies = new ArrayList<HTTPCookie>();
		
		for (String header : getHeaders("Set-Cookie")) {
			cookies.add(HTTPParser.parseCookie(header));
		}

		return cookies.toArray(new HTTPCookie[cookies.size()]);
	}
	
	@Override
	public boolean isResponse() {
		return true;
	}
	
	@Override
	public String toString () {
		String headerString = "";
		
		for (String header : headersList)
			headerString += "'" + header + "'\n";
		
		return String.format("### HTTP RESPONSE INFO ###\nResponse: %s, Version: %s\nHeaders: \n%s\nBody:\n'%s'\n### END OF HTTP RESPONSE INFO ###", response, version, headerString, body);
	}

	@Override
	public HTTPPacket cloneDeep() {
		HTTPResponse returnPacket = new HTTPResponse();
		
		returnPacket.response = response;
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
	
}
