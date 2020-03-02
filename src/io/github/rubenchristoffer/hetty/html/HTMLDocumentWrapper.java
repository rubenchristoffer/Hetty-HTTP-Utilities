package io.github.rubenchristoffer.hetty.html;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

/**
 * Class that acts as a wrapper for a HTML Document.
 * This means that all methods are being handled 
 * by the underlying Document interface (this class doesn't actually implement
 * any interface methods itself).
 * It also contains a few additional helper functions that are not part of the interface.
 * @author Ruben Christoffer
 */
public class HTMLDocumentWrapper extends HTMLNodeWrapper implements Document {
	
	private Document document;
	
	/**
	 * Creates a wrapper for the given HTML document
	 * @param document DOS model document
	 */
	public HTMLDocumentWrapper (Document document) {
		super(document);
		this.document = document;
	}
	
	// ### Wrapper methods ###
	
	public Node appendChild(Node newChild) throws DOMException { return document.appendChild(newChild); }
	public Node cloneNode(boolean deep) { return document.cloneNode(deep); }
	public short compareDocumentPosition(Node other) throws DOMException { return document.compareDocumentPosition(other); }
	public NamedNodeMap getAttributes() { return document.getAttributes(); }
	public String getBaseURI() { return document.getBaseURI(); }
	public NodeList getChildNodes() { return document.getChildNodes(); }
	public Object getFeature(String feature, String version) { return document.getFeature(feature, version); }
	public Node getFirstChild() { return document.getFirstChild(); }
	public Node getLastChild() { return document.getLastChild(); }
	public String getLocalName() { return document.getLocalName(); }
	public String getNamespaceURI() { return document.getNamespaceURI(); }
	public Node getNextSibling() { return document.getNextSibling(); }
	public String getNodeName() { return document.getNodeName(); }
	public short getNodeType() { return document.getNodeType(); }
	public String getNodeValue() throws DOMException { return document.getNodeValue(); }
	public Document getOwnerDocument() { return document.getOwnerDocument(); }
	public Node getParentNode() { return document.getParentNode(); }
	public String getPrefix() { return document.getPrefix(); }
	public Node getPreviousSibling() { return document.getPreviousSibling(); }
	public String getTextContent() throws DOMException { return document.getTextContent(); }
	public Object getUserData(String key) { return document.getUserData(key); }
	public boolean hasAttributes() { return document.hasAttributes(); }
	public boolean hasChildNodes() { return document.hasChildNodes(); }
	public Node insertBefore(Node newChild, Node refChild) throws DOMException { return document.insertBefore(newChild, refChild); }
	public boolean isDefaultNamespace(String namespaceURI) { return document.isDefaultNamespace(namespaceURI); }
	public boolean isEqualNode(Node arg) { return document.isEqualNode(arg); }
	public boolean isSameNode(Node other) { return document.isSameNode(other); }
	public boolean isSupported(String feature, String version) { return document.isSupported(feature, version); }
	public String lookupNamespaceURI(String prefix) { return document.lookupNamespaceURI(prefix); }
	public String lookupPrefix(String namespaceURI) { return document.lookupPrefix(namespaceURI); }
	public void normalize() { document.normalize(); }
	public Node removeChild(Node oldChild) throws DOMException { return document.removeChild(oldChild); }
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException { return document.replaceChild(newChild, oldChild); }
	public void setNodeValue(String nodeValue) throws DOMException { document.setNodeValue(nodeValue); }
	public void setPrefix(String prefix) throws DOMException { document.setPrefix(prefix); }
	public void setTextContent(String textContent) throws DOMException { document.setTextContent(textContent); }
	public Object setUserData(String key, Object data, UserDataHandler handler) { return document.setUserData(key, data, handler); }
	public Node adoptNode(Node source) throws DOMException { return document.adoptNode(source); }
	public Attr createAttribute(String name) throws DOMException { return document.createAttribute(name); }
	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException { return document.createAttributeNS(namespaceURI, qualifiedName); }
	public CDATASection createCDATASection(String data) throws DOMException { return document.createCDATASection(data); }
	public Comment createComment(String data) { return document.createComment(data); }
	public DocumentFragment createDocumentFragment() { return document.createDocumentFragment(); }
	public Element createElement(String tagName) throws DOMException { return document.createElement(tagName); }
	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException { return document.createElementNS(namespaceURI, qualifiedName); }
	public EntityReference createEntityReference(String name) throws DOMException { return document.createEntityReference(name); }
	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException { return document.createProcessingInstruction(target, data); }
	public Text createTextNode(String data) { return document.createTextNode(data); }
	public DocumentType getDoctype() { return document.getDoctype(); }
	public Element getDocumentElement() { return document.getDocumentElement(); }
	public String getDocumentURI() { return document.getDocumentURI(); }
	public DOMConfiguration getDomConfig() { return document.getDomConfig(); }
	public Element getElementById(String elementId) { return getElementById(elementId); }
	public NodeList getElementsByTagName(String tagname) { return document.getElementsByTagName(tagname); }
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) { return document.getElementsByTagNameNS(namespaceURI, localName); }
	public DOMImplementation getImplementation() { return document.getImplementation(); }
	public String getInputEncoding() { return document.getInputEncoding(); }
	public boolean getStrictErrorChecking() { return document.getStrictErrorChecking(); }
	public String getXmlEncoding() { return getXmlEncoding(); }
	public boolean getXmlStandalone() { return document.getXmlStandalone(); }
	public String getXmlVersion() { return document.getXmlVersion(); }
	public Node importNode(Node importedNode, boolean deep) throws DOMException { return document.importNode(importedNode, deep); }
	public void normalizeDocument() { document.normalizeDocument(); }
	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException { return document.renameNode(n, namespaceURI, qualifiedName); }
	public void setDocumentURI(String documentURI) { document.setDocumentURI(documentURI); }
	public void setStrictErrorChecking(boolean strictErrorChecking) { document.setStrictErrorChecking(strictErrorChecking); }
	public void setXmlStandalone(boolean xmlStandalone) throws DOMException { document.setXmlStandalone(xmlStandalone); }
	public void setXmlVersion(String xmlVersion) throws DOMException { document.setXmlVersion(xmlVersion); }
	public String toString () { return document.toString(); }
	
}
