package io.github.rubenchristoffer.hetty;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.rubenchristoffer.hetty.filters.HTTPPacketFilter;
import io.github.rubenchristoffer.hetty.misc.DeepCloneable;

/**
 * Class representing a HTTP packet / message.
 * This can be cast to either a HTTPRequest or
 * a HTTPResponse object. 
 * @author Ruben Christoffer
 */
public abstract class HTTPPacket implements DeepCloneable<HTTPPacket> {
	
	protected String version = "1.1";
	protected ArrayList<String> headersList = new ArrayList<String>();
	protected ByteArrayOutputStream body = new ByteArrayOutputStream();
	protected ArrayList<HTTPPacketFilter> filterPipeline = new ArrayList<HTTPPacketFilter>();
	
	/**
	 * Sets all the headers by one string.
	 * Headers are separated by delimiter.
	 * All cases of '\r' is removed from headers.
	 * @param multipleHeaders is a string containing multiple full headers seperated by delimiter
	 * @param delimiter is the string that determines how headers should be split. 
	 * This is usually '\n'
	 * @see #addHeader(String)
	 */
	public void setHeaders (String multipleHeaders, String delimiter) {
		for (String line : multipleHeaders.split(delimiter)) {
			line = line.replace("\r", "");
			addHeader (line);
		}
	}
	
	/**
	 * Adds a header that is properly formatted. 
	 * @param fullHeader is a string that is properly formatted and ready to add to packet
	 * without further processing
	 */
	public void addHeader (String fullHeader) {
		headersList.add(fullHeader);
	}
	
	/**
	 * Adds a header using name of header and its content. 
	 * @param name is the name of the header
	 * @param content is the content / value of the header
	 */
	public void addHeader (String name, String content) {
		headersList.add(name + ": " + content);
	}
	
	/**
	 * Inserts a header at the given index. 
	 * @param name is the name of the header
	 * @param content is the content / value of the header
	 * @param index is the index where you want to insert header
	 */
	public void insertHeader (String name, String content, int index) {
		headersList.add(index, name + ": " + content); 
	}
	
	/**
	 * Gets headers.
	 * @return read-only wrapper list that is always up-to-date
	 */
	public List<String> getHeaders () {
		return Collections.unmodifiableList(headersList);
	}

	/**
	 * Changes the first header content with the given name and content
	 * @param name is the name of the header
	 * @param content is the new content / value of the header
	 */
	public void changeHeader (String name, String content) {
		changeHeader(name, 0, content);
	}
	
	/**
	 * Changes the header content with the given name, content and index
	 * @param name is the name of the header
	 * @param index is the index of header in comparison to other headers with same name (if any)
	 * @param content is the new content / value of the header
	 */
	public void changeHeader (String name, int index, String content) {
		int count = 0;
		
		for (int i = 0; i < headersList.size(); i++) {
			if (headersList.get(i).toLowerCase().startsWith((name + ": ").toLowerCase())) {
				if (count == index) {
					headersList.set(i, name + ": " + content);
					return;
				} else {
					count++;
				}
			}
		}
	}
	
	/**
	 * Changes or adds a new header given whether or not the header exists or not. 
	 * @param name is the name of the header
	 * @param content is the content / value of the header
	 * @see #changeHeader(String, String)
	 * @see #addHeader(String, String)
	 */
	public void changeOrAddHeader (String name, String content) {
		if (doesHeaderExist(name)) {
			changeHeader(name, content);
		} else {
			addHeader(name, content);
		}
	}
	
	/**
	 * Changes or inserts a new header given whether or not the header exists or not.
	 * @param name is the name of the header
	 * @param content is the content / value of the header
	 * @param index is the index where you want to insert header (if header does not already exist)
	 * @see #changeHeader(String, String)
	 * @see #insertHeader(String, String, int)
	 */
	public void changeOrInsertHeader (String name, String content, int index) {
		if (doesHeaderExist(name)) {
			changeHeader(name, content);
		} else {
			insertHeader(name, content, index);
		}
	}
	
	/**
	 * Gets headers by name.
	 * @param name is the name of header(s)
	 * @return read-only list wrapper that is always up-to-date
	 */
	public List<String> getHeaders (String name) {
		ArrayList<String> returnHeadersList = new ArrayList<String>();
		
		for (int i = 0; i < headersList.size(); i++) {
			if (headersList.get(i).toLowerCase().startsWith((name + ": ").toLowerCase())) {
				returnHeadersList.add(headersList.get(i).split(": ", 2)[1]);
			}
		}
		
		return Collections.unmodifiableList(returnHeadersList);
	}
	
	/**
	 * Gets header content with the given name and content.
	 * @param name is the name of the header
	 * @param content is the content / value of the header
	 * @return header if found, null otherwise
	 */
	public String getHeader (String name, String content) {
		for (int i = 0; i < headersList.size(); i++) {
			if (headersList.get(i).toLowerCase().equals((name + ": " + content).toLowerCase())) {
				return headersList.get(i).split(": ", 2)[1];
			}
		}
		
		return null;
	}
	
	/**
	 * Returns true if any header with the provided name exists.
	 * @param name is the name of header
	 * @return true if header exists, false otherwise
	 * @see #getHeaders(String)
	 */
	public boolean doesHeaderExist (String name) {
		return getHeaders(name).size() > 0;
	}
	
	/**
	 * Returns true if the header with provided name exists and has the provided content
	 * @param name is the name of the header
	 * @param content is the content / value of the header
	 * @return true if header exists, false otherwise
	 * @see #getHeader(String, String)
	 */
	public boolean doesHeaderExist (String name, String content) {
		return getHeader(name, content) != null;
	}
	
	/**
	 * Removes the first header found by name.
	 * @param name is the name of the header
	 */
	public void removeHeader (String name) {
		removeHeader (name, 0);
	}
	
	/**
	 * Removes the header by name and index is used for telling which header should be removed if there are multiple
	 * @param name is the name of the header
	 * @param index is the index in comparison to other headers with same name
	 */
	public void removeHeader (String name, int index) {
		int count = 0;
		
		for (int i = 0; i < headersList.size(); i++) {
			if (headersList.get(i).toLowerCase().startsWith((name + ": ").toLowerCase())) {
				if (count == index) {
					headersList.remove(i);
					return;
				} else {
					count++;
				}
			}
		}
	}
	
	/**
	 * Removes header by name if it exists. 
	 * @param name is the name of the header
	 * @see #doesHeaderExist(String)
	 * @see #removeHeader(String)
	 */
	public void removeHeaderIfExists (String name) {
		if (doesHeaderExist(name)) {
			removeHeader(name);
		}
	}
	
	/**
	 * Sets the body of the HTTP packet.
	 * Use null or empty array for empty body.
	 * @param body is the byte array representing body of HTTP packet
	 */
	public void setBody (byte[] body) {
		this.body.reset();
	
		if (body != null) {
			this.body.write(body, 0, body.length);
		}
	}
	
	/**
	 * Gets the body of the HTTP packet.
	 * Note that this makes a copy of the internal body stream.
	 * @return the body expressed as byte array
	 */
	public byte[] getBody () {
		return body.toByteArray();
	}
	
	/**
	 * Gets body length (byte count).
	 * @return the length of internal byte stream
	 */
	public int getBodyLength () {
		return body.size();
	}
	
	/**
	 * Gets the internal body stream.
	 * Writing to this stream WILL update internal body stream.
	 * @return ByteArrayOutputStream that you can write to
	 */
	public ByteArrayOutputStream getBodyStream () {
		return body;
	}
	
	/**
	 * Sets the version of the HTTP packet. 
	 * @param version is the new version value you want to set
	 */
	public void setVersion (String version) {
		this.version = version;
	}
	
	/**
	 * Gets the version of the HTTP packet. 
	 * @return version value set for this packet
	 */
	public String getVersion () {
		return version;
	}
	
	/**
	 * Adds a new filter to the pipeline.
	 * This will add it to the end of the pipeline.
	 * @param filter is the packet filter you want to add
	 * @throws HTTPPacketException if a filter by that name already exists
	 */
	public void addFilter (HTTPPacketFilter filter) {
		if (getFilter(filter.getName()) != null)
			throw new HTTPPacketException(String.format("A filter with the name '%s' already exists", filter.getName()), null);
		
		filterPipeline.add(filter);
	}
	
	/**
	 * Inserts a new filter into the pipeline
	 * at a given index.
	 * @param filter is the packet filter you want to add
	 * @param index is the index where you want to insert filter
	 * @throws HTTPPacketException if a filter by that name already exists
	 */
	public void insertFilter (HTTPPacketFilter filter, int index) {
		if (getFilter(filter.getName()) != null)
			throw new HTTPPacketException(String.format("A filter with the name '%s' already exists", filter.getName()), null);
		
		filterPipeline.add(index, filter);
	}
	
	/**
	 * Gets filter by name. 
	 * @param name is the name of the filter
	 * @return HTTPPacketFilter if filter exists, null otherwise
	 */
	public HTTPPacketFilter getFilter (String name) {
		for (int i = 0; i < filterPipeline.size(); i++) {
			if (filterPipeline.get(i).getName().equals(name)) {
				return filterPipeline.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets filters in pipeline.
	 * @return read-only list wrapper that is always up-to-date
	 */
	public List<HTTPPacketFilter> getFilters () {
		return Collections.unmodifiableList(filterPipeline);
	}
	
	/**
	 * Removes a filter by name.
	 * @param name is the name of the filter you want to remove
	 */
	public void removeFilter (String name) {
		for (int i = 0; i < filterPipeline.size(); i++) {
			if (filterPipeline.get(i).getName().equals(name)) {
				filterPipeline.remove(i);
				break;
			}
		}
	}
	
	protected static void appendBytes (ByteArrayOutputStream builder, byte[] bytes) {
		builder.write(bytes, 0, bytes.length);
	}
	
	protected static void appendHeaderText (ByteArrayOutputStream builder, String headerText) {
		appendBytes(builder, headerText.getBytes(HTTPConfig.HEADER_CHARSET));
	}
	
	protected static void appendHeaders (ByteArrayOutputStream builder, HTTPPacket packet) {
		for (String header : packet.headersList) {
			appendHeaderText(builder, header + "\r\n");
		}
		
		appendHeaderText(builder, "\r\n");
	}
	
	/**
	 * Same as {@link #generatePacket(URL, boolean)} where applyFilters=true
	 * @param url is the URL you want to generate packet for
	 * @return HTTPRawPacket object
	 */
	public HTTPRawPacket generatePacket (URL url) {
		return generatePacket(url, true);
	}
	
	/**
	 * Generates a raw HTTP packet given a URL.
	 * No URL is needed for generating HTTP response packets.
	 * If you set applyFilters to true, it will pass packet through all HTTP Filters in the pipeline before generating packet.
	 * @param url is the URL you want to generate packet for
	 * @param applyFilters determines whether filters will be applied before generating packet
	 * @return HTTPRawPacket object
	 */
	public abstract HTTPRawPacket generatePacket (URL url, boolean applyFilters);
	
	/**
	 * Permanently applies all filters in pipeline.
	 * No URL is needed for filtering HTTP response packets.
	 * @param url is the URL you want to apply filters for
	 */
	public void applyFilters (URL url) {
		for (int i = 0; i < filterPipeline.size(); i++) {
			filterPipeline.get(i).filter(this, isResponse() ? null : url);
		}
	}
	
	/**
	 * Creates a cloned instance of packet and applies all filters to it
	 * @param packet is the packet you want to filter
	 * @param url is the URL you want to apply filters for
	 * @return HTTPPacket clone that has all filteres applied
	 */
	public static HTTPPacket filterAll (HTTPPacket packet, URL url) {
		HTTPPacket returnPacket = (HTTPPacket) packet.cloneDeep();
		returnPacket.applyFilters(url);
		
		return returnPacket;
	}
	
	/**
	 * Gets cookies.
	 * @return Array of all cookies found in packet
	 */
	public abstract HTTPCookie[] getCookies ();
	
	/**
	 * Is this packet a response or request?
	 * @return true if instance of HTTPResponse, false if instance of HTTPRequest
	 */
	public abstract boolean isResponse();
	
}