package rubenchristoffer.hetty.html;

import org.w3c.dom.Document;

/**
 * Abstract wrapper class for a HTML parser.
 * This can be used to integrate any kind of HTML parser
 * with the API as long as it supports the w3c DOM model.
 * @author Ruben Christoffer
 */
public abstract class HTMLParser {

	/**
	 * Parses the HTML string into a DOM document.
	 * @param html is a string containing HTML code
	 * @return DOM document
	 */
	public abstract Document parseDocument (String html);
	
	/**
	 * Parses the HTML string into a DOM document
	 * and wraps it using the wrapper class
	 * HTMLDocumentWrapper.
	 * Same as {@link #parseDocument(String)} and 
	 * {@link rubenchristoffer.hetty.html.HTMLDocumentWrapper#HTMLDocumentWrapper(Document)}
	 * @param html is a string containing HTML code
	 * @return DOM document wrapped in HTMLDocumentWrapper class
	 */
	public HTMLDocumentWrapper parseAndWrapDocument (String html) {
		return new HTMLDocumentWrapper(parseDocument(html));
	}
	
}
