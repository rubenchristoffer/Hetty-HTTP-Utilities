package rubenchristoffer.hetty;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rubenchristoffer.hetty.filters.HTTPPacketFilter;

public abstract class HTTPPacket {
	
	protected String responseRequest = "";
	protected String version = "1.0";
	protected ArrayList<String> headers = new ArrayList<String>();
	protected String body = "";
	
	protected ArrayList<HTTPPacketFilter> filterPipeline = new ArrayList<HTTPPacketFilter>();
	
	/**
	 * Sets all the headers by one string.
	 * Headers are separated by new-line character ('\n').
	 * @param multipleHeaders
	 */
	public void SetHeaders (String multipleHeaders) {
		for (String line : multipleHeaders.split("\n")) {
			line = line.replace("\r", "");
			AddHeader (line);
		}
	}
	
	/**
	 * Adds a header that is properly formatted. 
	 * @param fullHeader
	 */
	public void AddHeader (String fullHeader) {
		headers.add(fullHeader);
	}
	
	/**
	 * Adds a header using name of header and its content. 
	 * @param name
	 * @param content
	 */
	public void AddHeader (String name, String content) {
		headers.add(name + ": " + content);
	}
	
	/**
	 * Inserts a header at the given index. 
	 * @param name
	 * @param content
	 * @param index
	 */
	public void InsertHeader (String name, String content, int index) {
		headers.add(index, name + ": " + content); 
	}
	
	/**
	 * Gets a read-only list wrapper that automatically updates when list is updated.
	 * @return
	 */
	public List<String> GetHeaders () {
		return Collections.unmodifiableList(headers);
	}

	/**
	 * Changes the first header content with the given name and content
	 * @param name
	 * @param content
	 */
	public void ChangeHeader (String name, String content) {
		ChangeHeader(name, 0, content);
	}
	
	/**
	 * Changes the header content with the given name, content and index
	 * @param name
	 * @param index
	 * @param content
	 * @throws IndexOutOfBoundsException
	 */
	public void ChangeHeader (String name, int index, String content) throws IndexOutOfBoundsException {
		int count = 0;
		
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).toLowerCase().startsWith((name + ": ").toLowerCase())) {
				if (count == index) {
					headers.set(i, name + ": " + content);
					return;
				} else {
					count++;
				}
			}
		}
		
		if (count == 0) {
			throw new IndexOutOfBoundsException("There are no headers with name '" + name + "', but provided index was " + index);
		} else {
			throw new IndexOutOfBoundsException("There are only " + count + " headers with name '" + name + "', but provided index was " + index);
		}
	}
	
	/**
	 * Changes or adds a new header given whether or not the header exists or not. 
	 * @param name
	 * @param content
	 */
	public void ChangeOrAddHeader (String name, String content) {
		if (DoesHeaderExist(name)) {
			ChangeHeader(name, content);
		} else {
			AddHeader(name, content);
		}
	}
	
	/**
	 * Changes or inserts a new header given whether or not the header exists or not.
	 * @param name
	 * @param content
	 * @param index
	 */
	public void ChangeOrInsertHeader (String name, String content, int index) {
		if (DoesHeaderExist(name)) {
			ChangeHeader(name, content);
		} else {
			InsertHeader(name, content, index);
		}
	}
	
	/**
	 * Gets a read-only list wrapper containing headers with the given name
	 * NOTE: This does NOT update when original header list is updated.
	 * @param name
	 */
	public List<String> GetHeaders (String name) {
		ArrayList<String> returnHeadersList = new ArrayList<String>();
		
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).toLowerCase().startsWith((name + ": ").toLowerCase())) {
				returnHeadersList.add(headers.get(i).split(": ", 2)[1]);
			}
		}
		
		return Collections.unmodifiableList(returnHeadersList);
	}
	
	/**
	 * Gets header content with the given name and content
	 * @param name
	 * @param content
	 */
	public String GetHeader (String name, String content) {
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).toLowerCase().equals((name + ": " + content).toLowerCase())) {
				return headers.get(i).split(": ", 2)[1];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns true if the header with provided name exists
	 * @param name
	 */
	public boolean DoesHeaderExist (String name) {
		return GetHeaders(name).size() > 0;
	}
	
	/**
	 * Returns true if the header with provided name exists and has the provided content
	 * @param name
	 */
	public boolean DoesHeaderExist (String name, String content) {
		return GetHeader(name, content) != null;
	}
	
	/**
	 * Removes the first header by name
	 * @param name
	 */
	public void RemoveHeader (String name) throws IndexOutOfBoundsException {
		RemoveHeader (name, 0);
	}
	
	/**
	 * Removes the header by name and index is used for telling which header should be removed if there are multiple
	 * @param name
	 * @param index
	 * @throws IndexOutOfBoundsException
	 */
	public void RemoveHeader (String name, int index) throws IndexOutOfBoundsException {
		int count = 0;
		
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).toLowerCase().startsWith((name + ": ").toLowerCase())) {
				if (count == index) {
					headers.remove(i);
					return;
				} else {
					count++;
				}
			}
		}
		
		if (count == 0) {
			throw new IndexOutOfBoundsException("There are no headers with name '" + name + "', but provided index was " + index);
		} else {
			throw new IndexOutOfBoundsException("There are only " + count + " headers with name '" + name + "', but provided index was " + index);
		}
	}
	
	/**
	 * Sets the body of the HTTP packet.
	 * Use "" or null for empty body.
	 * @param content
	 */
	public void SetBody (String content) {
		body = content;
		
		if (body == null)
			body = "";
	}
	
	/**
	 * Gets the body of the HTTP packet.
	 * Returns "" for empty body. 
	 */
	public String GetBody () {
		return body;
	}
	
	/**
	 * Sets the version of the HTTP packet. 
	 * @param version
	 */
	public void SetVersion (String version) {
		this.version = version;
	}
	
	/**
	 * Gets the version of the HTTP packet. 
	 * @param version
	 */
	public String GetVersion () {
		return version;
	}
	
	public void AddFilter (HTTPPacketFilter filter) {
		filterPipeline.add(filter);
	}
	
	public void InsertFilter (HTTPPacketFilter filter, int index) {
		filterPipeline.add(index, filter);
	}
	
	public HTTPPacketFilter GetFilter (String name) {
		for (int i = 0; i < filterPipeline.size(); i++) {
			if (filterPipeline.get(i).GetName().equals(name)) {
				return filterPipeline.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a read-only list wrapper that automatically updates when list is updated.
	 * @return
	 */
	public List<HTTPPacketFilter> GetFilters () {
		return Collections.unmodifiableList(filterPipeline);
	}
	
	public void RemoveFilter (String name) {
		for (int i = 0; i < filterPipeline.size(); i++) {
			if (filterPipeline.get(i).GetName().equals(name)) {
				filterPipeline.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Parses raw HTTP packet (pure string) into either a HTTPRequest or HTTPResponse object. 
	 * You can use casting to retrieve one of those objects. 
	 * @param rawHttpPacket
	 */
	public static HTTPPacket ParseRawPacket (String rawHttpPacket) {
		HTTPPacket returnPacket = null;
		
		boolean isResponse = false;
		String[] lines = rawHttpPacket.split("\r\n", -1); // -1 means that empty lines ("") are included
		
		if (lines[0].toLowerCase().startsWith("http")) {
			isResponse = true;
		}
		
		if (isResponse) {
			HTTPResponse response = new HTTPResponse();
			
			if (lines[0].charAt(4) == '/')
				response.version = lines[0].split(" ")[0].split("/")[1];
			
			response.responseRequest = lines[0].split(" ", 2)[1];
			
			response = (HTTPResponse) AddHeaderAndBody (lines, response);
			
			returnPacket = response;
		} else {
			HTTPRequest request = new HTTPRequest();
			request.responseRequest = lines[0].split(" ")[0];
			
			if (lines[0].endsWith("1.1"))
				request.version = "1.1";
			
			request = (HTTPRequest) AddHeaderAndBody(lines, request);
			
			returnPacket = request;
		}
		
		return returnPacket;
	}
	
	private static HTTPPacket AddHeaderAndBody (String[] lines, HTTPPacket packet) {
		int headerEnd = -1;
		
		for (int i = 1; i < lines.length; i++) {
			if (lines[i].equals("")) {
				headerEnd = i;
				break;
			}
			
			packet.AddHeader (lines[i]);
		}
		
		if (headerEnd != -1) {
			for (int i = headerEnd + 1; i < lines.length; i++) {
				packet.body += lines[i];
			}
		}
		
		return packet;
	}
	
	protected static StringBuilder AppendHeadersAndBody (HTTPPacket packet, StringBuilder builder) {
		for (String header : packet.headers) {
			builder.append(header + "\r\n");
		}
		
		builder.append("\r\n");
		
		if (!packet.body.equals("")) {
			builder.append(packet.body + "\r\n");
		}
		
		builder.append("\r\n");
		
		return builder;
	}
	
	/**
	 * Generates a raw HTTP packet (pure string) given a URL.
	 * No URL is needed for generating HTTP response packets.
	 * Sets useFilters to true.
	 * @param url
	 */
	public String GeneratePacket (URL url) {
		return GeneratePacket(url, true);
	}
	
	/**
	 * Generates a raw HTTP packet (pure string) given a URL.
	 * No URL is needed for generating HTTP response packets.
	 * If you set applyFilters to true, it will pass packet through all HTTP Filters in the pipeline before generating packet.
	 * @param url
	 * @param automaticHeaderInsertions
	 */
	public String GeneratePacket (URL url, boolean applyFilters) {
		HTTPPacket packet = this;
		
		if (applyFilters) {
			packet = FilterAll(packet, url);
		}
		
		if (IsResponse()) {
			return HTTPResponse.GeneratePacket(packet, url);
		} else {
			return HTTPRequest.GeneratePacket(packet, url);
		}
	}
	
	/**
	 * Permanently applies all filters in pipeline.
	 * No URL is needed for filtering HTTP response packets.
	 */
	public void ApplyFilters (URL url) {
		for (int i = 0; i < filterPipeline.size(); i++) {
			if (IsResponse()) {
				filterPipeline.get(i).Filter(this, null);
			} else {
				filterPipeline.get(i).Filter(this, url);
			}
		}
	}
	
	/**
	 * Creates a cloned instance of packet and applies all filters to it
	 * @param packet
	 * @param url
	 * @return HTTPPacket
	 */
	public static HTTPPacket FilterAll (HTTPPacket packet, URL url) {
		HTTPPacket returnPacket = (HTTPPacket) packet.Clone();
		returnPacket.ApplyFilters(url);
		
		return returnPacket;
	}
	
	public abstract HTTPCookie[] ParseCookies ();
	public abstract boolean IsResponse();
	
	/**
	 * Makes '\n' and '\r' characters visible. Useful for debugging. 
	 * @param rawHttpPacket
	 */
	public static String UnEscapeRawPacket (String rawHttpPacket){
	    StringBuilder sb = new StringBuilder();
	    
	    for (int i = 0; i < rawHttpPacket.length(); i++) {
	        switch (rawHttpPacket.charAt(i)){
	            case '\n': sb.append("\\n"); break;
	            case '\r': sb.append("\\r"); break;
	            
	            default: sb.append(rawHttpPacket.charAt(i));
	        }
	    }
	    
	    return sb.toString();
	}
	
	/**
	 * Provides useful information about HTTP Packet. 
	 */
	@Override
	public String toString () {
		String headerString = "";
		
		for (String header : headers)
			headerString += "'" + header + "'\n";
		
		return "Response / Request: " + responseRequest + ", Version: " + version + "\nHeaders:\n" + headerString + "\nBody:\n'" + body + "'";
	}
	
	/*
	 * 
	 * protected String responseRequest = "";
	protected String version = "1.0";
	protected ArrayList<String> headers = new ArrayList<String>();
	protected String body = "";
	
	protected ArrayList<HTTPPacketFilter> filterPipeline = new ArrayList<HTTPPacketFilter>();
	 */
	
	public HTTPPacket Clone() {
		HTTPPacket returnPacket = null;
		
		if (IsResponse()) {
			returnPacket = new HTTPResponse();
		} else {
			returnPacket = new HTTPRequest();
		}
		
		returnPacket.responseRequest = responseRequest;
		returnPacket.version = version;
		
		for (int i = 0; i < headers.size(); i++) {
			returnPacket.headers.add(headers.get(i));
		}
		
		returnPacket.body = body;
		
		for (int i = 0; i < filterPipeline.size(); i++) {
			returnPacket.filterPipeline.add(filterPipeline.get(i).Clone());
		}
		
		return returnPacket;
	}
	
}