package rubenchristoffer.hetty.filters;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rubenchristoffer.hetty.HTTPConfig;
import rubenchristoffer.hetty.HTTPPacket;
import rubenchristoffer.hetty.HTTPRequest;

/**
 * HTTP filter that aids in posting forms.
 * It will replace body with properly encoded body
 * (uses {@link rubenchristoffer.hetty.HTTPConfig#FORM_DATA_CHARSET}
 * for encoding) based on formdata and set header content-type to 
 * 'application/x-www-form-urlencoded'.
 * @author Ruben Christoffer
 */
public class HTTPFormDataFilter extends HTTPPacketFilter {

	private Map<String, String> formData;
	
	/**
	 * Creates a new HTTPFormDataFilter.
	 * @param name is the name of the filter
	 * @param formData is the form data that you wish to post in the form {@literal <name, value>}
	 */
	public HTTPFormDataFilter(String name, Map<String, String> formData) {
		super(name);
		
		this.formData = formData;
	}
	
	@Override
	public void filter(HTTPPacket packet, URL url) {
		if (!packet.isResponse() && ((HTTPRequest) packet).getRequestMethod().equalsIgnoreCase("post")) {
			Iterator<String> keyIterator = formData.keySet().iterator();
			
			packet.changeOrAddHeader("content-type", "application/x-www-form-urlencoded");
			packet.getBodyStream().reset();
			
			try {
				while (keyIterator.hasNext()) {
					String key = keyIterator.next();
					
					byte[] toWrite = (key + "=" + URLEncoder.encode(formData.get(key), HTTPConfig.FORM_DATA_CHARSET) + (keyIterator.hasNext() ? "&" : "")).getBytes(HTTPConfig.FORM_DATA_CHARSET);
					packet.getBodyStream().write (toWrite, 0, toWrite.length);
				}
			} catch (UnsupportedEncodingException e) {
				throw new HTTPFilterException("Could not encode form data because encoder"
						+ "provider could not be found or is not supported", e);
			}
		}
	}

	@Override
	public HTTPPacketFilter cloneDeep() {
		HashMap<String, String> formData = new HashMap<String, String>();
		
		for (String key : this.formData.keySet()) {
			formData.put(key, this.formData.get(key));
		}
		
		return new HTTPFormDataFilter(name, formData);
	}
	
}
