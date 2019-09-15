package rubenchristoffer.hetty;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocketFactory;

public class HTTPConnection {

	private URL url;
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	
	public HTTPConnection (URL url) {
		this.url = url;
	}
	
	public HTTPConnection (String url) throws MalformedURLException {
		this.url = new URL(url);
	}
	
	/**
	 * Opens a TCP / SSL socket and connects to the server.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void OpenConnection () throws UnknownHostException, IOException {
		// Set port to 443 (if HTTPS) or 80 (if HTTP) unless port is specified
		int port = url.getPort() == -1 ? (url.getProtocol().toLowerCase().equals("https") ? 443 : 80) : url.getPort();
		
		if (url.getProtocol().toLowerCase().equals("https")) {
			socket = SSLSocketFactory.getDefault().createSocket(url.getHost(), port);
		} else {
			socket = new Socket (url.getHost(), port);
		}
		
		socket.setTcpNoDelay(true);
		
		writer = new BufferedWriter (new OutputStreamWriter (socket.getOutputStream(), StandardCharsets.UTF_8));
		reader = new BufferedReader (new InputStreamReader (socket.getInputStream(), StandardCharsets.UTF_8));
	}
	
	/**
	 * Returns true if the connection is open.
	 * Returns false if the socket is null or closed
	 */
	public boolean IsConnectionOpen () {
		if (socket == null || socket.isClosed())
			return false;
		
		return true;
	}
	
	/**
	 * Closes the TCP / SSL socket. 
	 * @throws IOException
	 */
	public void CloseConnection () throws IOException {
		socket.close();
	}
	
	/**
	 * Sends a HTTP packet (either a HTTPRequest or HTTPResponse) to the server.
	 * Applying filters is set to true. 
	 * @param packet
	 * @throws IOException
	 */
	public void SendPacket (HTTPPacket packet) throws IOException {
		SendRawPacket (packet.GeneratePacket(url));
	}

	/**
	 * Sends a HTTP packet (either a HTTPRequest or HTTPResponse) to the server.
	 * Applying filters is optional. 
	 * @param packet
	 * @throws IOException
	 */
	public void SendPacket (HTTPPacket packet, boolean applyFilters) throws IOException {
		SendRawPacket (packet.GeneratePacket(url, applyFilters));
	}
	
	/**
	 * Sends a raw HTTP packet (pure string) to the server.
	 * @param rawHttpPacket
	 * @throws IOException
	 */
	public void SendRawPacket (String rawHttpPacket) throws IOException {
		writer.write(rawHttpPacket);
		writer.flush();
	}
	
	/**
	 * Reads the raw HTTP packet (pure string) from the server. 
	 * @throws IOException
	 */
	public String ReadRawPacket () throws IOException {
		StringBuilder returnStringBuilder = new StringBuilder();
		
		char character;
		StringBuilder lineBuilder = new StringBuilder();
		
		boolean startBody = false;
		int contentCount = 0;
		
		int contentLength = -1;
		
		boolean chunkedTransfer = false;
		int chunkLength = -1;
		
		while (!socket.isClosed()) {
			int read = reader.read();
			
			if (read == -1) { // Reached end of stream
				break;
			}
			
			character = (char) read;
			lineBuilder.append(character);
			
			if (startBody && contentLength != -1 && !chunkedTransfer)
				contentCount++;
				
			if (character == '\n') {
				String line = lineBuilder.toString();
				
				if (!startBody && line.toLowerCase().startsWith("content-length") && contentLength == -1) {
					contentLength = Integer.parseInt(line.split(":")[1].trim());
				}
				
				if (!startBody && line.toLowerCase().startsWith("transfer-encoding: chunked")) {
					chunkedTransfer = true;
				}
				
				if (line.equals("\r\n")) {
					startBody = true;
				}
				
				returnStringBuilder.append(line);
				lineBuilder = new StringBuilder();
			}
			
			if (startBody && contentCount == contentLength) {
				break;
			}
		}
		
		if (lineBuilder.toString().length() > 0)
			returnStringBuilder.append(lineBuilder.toString());
		
		return returnStringBuilder.toString();
	}
	
	/**
	 * Reads the raw HTTP packet (pure string) from the server and parses it to create a HTTPPacket.
	 * You can cast this to either HTTPRequest or HTTPResponse.
	 * @throws IOException
	 */
	public HTTPPacket ReadPacket () throws IOException {
		return HTTPPacket.ParseRawPacket(ReadRawPacket());
	}
	
	/**
	 * Sets the current URL used by this connection.
	 * @return
	 */
	public void SetURL (URL url) {
		this.url = url;
	}
	
	/**
	 * Gets the current URL used by this connection.
	 * @return
	 */
	public URL GetURL () {
		return url;
	}
	
	/**
	 * Gets the underlying TCP / SSL socket. 
	 */
	public Socket GetUnderlyingSocket () {
		return socket;
	}
	
}
