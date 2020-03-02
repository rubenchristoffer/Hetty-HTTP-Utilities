package io.github.rubenchristoffer.hetty.thirdparty;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;

import io.github.rubenchristoffer.hetty.html.HTMLParser;

/**
 * This is a wrapper class for the 
 * HTML parser Jsoup. Requires the
 * Jsoup library to work.
 * @author Ruben Christoffer
 * @see <a href="https://jsoup.org/">https://jsoup.org/</a>
 */
public class JsoupParser extends HTMLParser {

	@Override
	public Document parseDocument(String html) {
		return new W3CDom().fromJsoup(Jsoup.parse(html));
	}

}
