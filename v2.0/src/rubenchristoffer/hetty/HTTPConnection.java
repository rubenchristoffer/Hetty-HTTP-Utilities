package rubenchristoffer.hetty;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import rubenchristoffer.hetty.codec.HTTPChunkedTransferDecoder;
import rubenchristoffer.hetty.codec.HTTPContentLengthTransferDecoder;
import rubenchristoffer.hetty.codec.HTTPTransferDecoder;
import rubenchristoffer.hetty.codec.HTTPTransferDecoder.DecodeInfo;
import rubenchristoffer.hetty.codec.HTTPUnsupportedContentException;
import rubenchristoffer.hetty.validation.ArgumentValidator;

/**
 * Class that handles underlying TCP / TLS connection to server. 
 * You can use this class for sending / receiving individual HTTP messages / packets.
 * It is also possible to access the underlying socket or write raw bytes.
 * This is the first layer in the HTTP API and may be used directly or indirectly through
 * higher-level classes.
 * @author Ruben Christoffer
 */
public class HTTPConnection {

	private URL url;
	private ArrayList<HTTPTransferDecoder> supportedTransferDecoders = new ArrayList<HTTPTransferDecoder> ();

	private Socket socket;
	private BufferedInputStream inStream;
	private BufferedOutputStream outStream;

	/**
	 * Creates a new HTTPConnection object with url=null and addChunkedTransferDecoder=true.
	 */
	public HTTPConnection () {
		initialize (null, true);
	}

	/**
	 * Creates a new HTTPConnection object with url=null.
	 * @param addChunkedTransferDecoder is used for determining if chunked transfer codec should be supported
	 */
	public HTTPConnection (boolean addChunkedTransferDecoder) {
		initialize (null, addChunkedTransferDecoder);
	}

	/**
	 * Creates a new HTTPConnection object with addChunkedTransferDecoder=true.
	 * @param url is the URL of the server you wish to connect to
	 */
	public HTTPConnection (URL url) {
		initialize (url, true);
	}

	/**
	 * Creates a new HTTPConnection object with addChunkedTransferDecoder=true.
	 * @param url is the URL of the server you wish to connect to
	 * @throws IllegalArgumentException if URL is invalid
	 */
	public HTTPConnection (String url) {
		try {
			initialize (new URL (url), true);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException ("URL is invalid.", e);
		}
	}

	/**
	 * Creates a new HTTPConnection object.
	 * @param url is the URL of the server you wish to connect to
	 * @param addChunkedTransferDecoder is used for determining if chunked transfer codec should be supported
	 */
	public HTTPConnection (URL url, boolean addChunkedTransferDecoder) {
		initialize (url, addChunkedTransferDecoder);
	}

	private void initialize (URL url, boolean addChunkedTransferDecoder) {
		this.url = url;

		// Always add content length decoder first
		addSupportedTransferDecoder (new HTTPContentLengthTransferDecoder ());

		if (addChunkedTransferDecoder)
			addSupportedTransferDecoder (new HTTPChunkedTransferDecoder ());
	}

	// ### Functions for managing transfer decoders ###

	/**
	 * Adds a new supported transfer decoder for this connection.
	 * @param transferCodec is the codec you want this connection to support
	 * @see HTTPTransferDecoder for information on how decoders are chosen when reading packets
	 */
	public void addSupportedTransferDecoder (HTTPTransferDecoder transferCodec) {
		supportedTransferDecoders.add (transferCodec);
	}

	/**
	 * Inserts a new supported transfer decoder for this connection
	 * at a specific index of the list. 
	 * @param transferCodec is the codec you want this connection to support
	 * @param index is the index where you want the decoder to be inserted
	 */
	public void insertSupportedTransferDecoder (HTTPTransferDecoder transferCodec, int index) {
		supportedTransferDecoders.add (index, transferCodec);
	}

	/**
	 * Removes a supported transfer decoder for this connection
	 * @param index is the index of the decoder you want to remove
	 */
	public void removeSupportedTransferDecoder (int index) {
		supportedTransferDecoders.remove (index);
	}

	/**
	 * Gets supported transfer decoders. 
	 * @return a read-only wrapper list of the supported decoders that is always up-to-date
	 */
	public List<HTTPTransferDecoder> getSupportedTransferDecoders () {
		return Collections.unmodifiableList (supportedTransferDecoders);
	}

	/**
	 * Opens a TCP / SSL socket and connects to the server.
	 * It will automatically detect if you're trying to connect
	 * using HTTP or HTTPS.
	 * @throws HTTPConnectionException if something goes wrong
	 * opening the connection
	 */
	public void openConnection () {
		// Set port to 443 (if HTTPS) or 80 (if HTTP) unless port is specified
		int port = url.getPort () == -1 ? (url.getProtocol ().toLowerCase ().equals ("https") ? 443 : 80)
				: url.getPort ();

		if (url.getProtocol ().toLowerCase ().equals ("https")) {
			try {
				socket = SSLSocketFactory.getDefault ().createSocket (url.getHost (), port);
			} catch (IOException e) {
				throw new HTTPConnectionException (
						String.format ("Could not open HTTPS connection on port %d." + " Perhaps URL is wrong?", port),
						e);
			}
		} else {
			try {
				socket = new Socket (url.getHost (), port);
			} catch (IOException e) {
				throw new HTTPConnectionException (
						String.format ("Could not open HTTP connection on port %d." + " Perhaps URL is wrong?", port),
						e);
			}
		}

		try {
			socket.setTcpNoDelay (true);
		} catch (SocketException e) {
			throw new HTTPConnectionException ("Could not enable No Delay feature of TCP connection", e);
		}

		try {
			inStream = new BufferedInputStream (socket.getInputStream ());
			outStream = new BufferedOutputStream (socket.getOutputStream ());
		} catch (IOException e) {
			throw new HTTPConnectionException ("Could not initialize input / output streams", e);
		}
	}

	/**
	 * Is the connection open?
	 * @return true if the connection is open, false if the socket is null
	 * or closed
	 */
	public boolean isConnectionOpen () {
		if (socket == null)
			return false;

		return !socket.isClosed ();
	}

	/**
	 * Closes the TCP / SSL socket.
	 * @throws HTTPConnectionException if it could not close connection
	 */
	public void closeConnection () {
		try {
			socket.close ();
		} catch (IOException e) {
			throw new HTTPConnectionException ("Could not close connection", e);
		}
	}

	/**
	 * Sends a HTTP packet (either a HTTPRequest or HTTPResponse) to the server.
	 * Applying filters is set to true.
	 * @param packet is the packet you want to send
	 */
	public void sendPacket (HTTPPacket packet) {
		sendPacket (packet, true);
	}

	/**
	 * Sends a HTTP packet (either a HTTPRequest or HTTPResponse) to the server.
	 * @param packet is the packet you want to send
	 * @param applyFilters determines whether filters will be applied before sending packet
	 * @throws HTTPConnectionException if something went wrong sending HTTP packet
	 */
	public void sendPacket (HTTPPacket packet, boolean applyFilters) {
		ArgumentValidator.requireNonNullArgument (packet, "packet cannot be null");

		try {
			sendRawPacket (packet.generatePacket (url, applyFilters));
		} catch (HTTPConnectionException e) {
			throw new HTTPConnectionException ("Something went wrong sending HTTP packet", e.getCause ());
		}
	}

	/**
	 * Sends a raw HTTP packet to the server.
	 * @param rawHttpPacket is the packet you want to send
	 * @throws HTTPConnectionException if something goes wrong sending raw packet
	 * @throws IllegalArgumentException if rawHttpPacket is null
	 */
	public void sendRawPacket (HTTPRawPacket rawHttpPacket) throws HTTPConnectionException {
		ArgumentValidator.requireNonNullArgument (rawHttpPacket, "rawHttpPacket cannot be null");

		try {
			sendRawBytes (rawHttpPacket.toByteArray ());
		} catch (HTTPConnectionException e) {
			throw new HTTPConnectionException ("Something went wrong sending raw HTTP packet", e);
		}
	}

	/**
	 * Sends raw byte array to server.
	 * @param bytes is the byte array you want to send
	 * @throws HTTPConnectionException if something goes wrong sending raw byte array
	 */
	public void sendRawBytes (byte[] bytes) throws HTTPConnectionException {
		ArgumentValidator.requireNonEmptyByteArrayArgument (bytes, "bytes cannot be null or empty array");

		try {
			outStream.write (bytes);
			outStream.flush ();
		} catch (IOException e) {
			throw new HTTPConnectionException ("Something went wrong sending data", e);
		} catch (NullPointerException e) {
			throw new HTTPConnectionException ("OutputStream is null. Perhaps the connection is not open?", e);
		}
	}

	/**
	 * Reads the raw HTTP packet from the server.
	 * This will first read the entire HTTP header. 
	 * After reading header, it will start at the bottom of the list of
	 * supported decoders and use the first available decoder. 
	 * @return a raw HTTP packet
	 * @throws HTTPConnectionException if something goes wrong with the connection
	 * @throws HTTPUnsupportedContentException if no decoders support decoding the body
	 */
	public HTTPRawPacket readRawPacket () {
		ByteArrayOutputStream headerStream = new ByteArrayOutputStream (1024);
		ByteArrayOutputStream bodyStream = new ByteArrayOutputStream ();
		char character;
		StringBuilder lineBuilder = new StringBuilder ();
		HashMap<String, String> headers = new HashMap<String, String> ();

		boolean startBody = false;
		boolean finishedBody = false;
		HTTPTransferDecoder transferDecoder = null;

		while (!socket.isClosed () && !finishedBody) {
			int read;

			try {
				read = inStream.read ();
			} catch (IOException e) {
				throw new HTTPConnectionException ("Something went wrong reading raw HTTP packet", e);
			}

			// Reached end of stream
			if (read == -1) {
				break;
			}

			if (!startBody) {
				// Read header first
				headerStream.write (read);
				character = (char) read;
				lineBuilder.append (character);

				if (character == '\n') {
					String line = lineBuilder.toString ();

					// Check if reached end of header
					if (line.equals ("\r\n")) {
						startBody = true;

						// If so, Determine which transfer decoder to use
						for (int i = 0; i < supportedTransferDecoders.size (); i++) {
							DecodeInfo status = supportedTransferDecoders.get (i).getDecodeInfo (headers);

							if (status == DecodeInfo.CAN_DECODE) {
								transferDecoder = supportedTransferDecoders.get (i);
								break;
							} else if (status == DecodeInfo.EMPTY_BODY) {
								finishedBody = true;
								break;
							}
						}

						// If no transfer decoders can decode body then throw exception
						if (transferDecoder != null) {
							transferDecoder.initialize (headers);
						} else {
							throw new HTTPUnsupportedContentException ("There were no available decoders in the list of supported decoders."
									+ " The rest of the content will not be read by socket!", null);
						}
					} else {
						// Add header to hashmap (if it is a header)
						String[] split = line.split (":", 2);

						if (split.length == 2) {
							// Trim actually removes \r for us, which is great
							headers.put (split[0].toLowerCase (), split[1].trim ().toLowerCase ());
						}

						lineBuilder.setLength (0);
					}
				}
			} else {
				// Then read body using a valid transfer decoder and stop when decoder has
				// detected end of body
				finishedBody = transferDecoder.decodeNext (bodyStream, read);
			}
		}

		return new HTTPRawPacket (headerStream, bodyStream);
	}

	/**
	 * Reads the raw HTTP packet from the server and parses it to create a
	 * HTTPPacket. You can cast this to either HTTPRequest or HTTPResponse.
	 * This method is a combination of {@link #readRawPacket()}
	 * and {@link rubenchristoffer.hetty.HTTPParser#parsePacket(HTTPRawPacket)}
	 * @return HTTPPacket object
	 */
	public HTTPPacket readPacket () {
		return HTTPParser.parsePacket (readRawPacket ());
	}

	/**
	 * Sets the current URL used by this connection.
	 * Will try to close connection if host of URL is different
	 * from current URL host.
	 * @param url is the URL you want to set
	 * @throws HTTPConnectionException if URL host is different and closing connection failed
	 */
	public void setURL (URL url) throws HTTPConnectionException {
		if (this.url != null && url != null) {
			if (!url.getHost ().equalsIgnoreCase (this.url.getHost ())) {
				try {
					closeConnection ();
				} catch (HTTPConnectionException e) {
					throw new HTTPConnectionException (
							"Could not close connection when setting new URL with different host.", e);
				}
			}
		}

		this.url = url;
	}

	/**
	 * Gets the URL.
	 * @return the current URL used by this connection.
	 */
	public URL getURL () {
		return url;
	}

	/**
	 * Gets the underlying socket.
	 * @return the underlying TCP / SSL socket
	 */
	public Socket getUnderlyingSocket () {
		return socket;
	}

}
