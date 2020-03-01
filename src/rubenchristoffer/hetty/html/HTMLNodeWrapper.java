package rubenchristoffer.hetty.html;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * Wrapper class for HTML DOM Node.
 * Allows for iteration (in a for-each loop or using iterator)
 * Also has a few helper functions.
 * @author Ruben Christoffer
 */
public class HTMLNodeWrapper implements Node, Iterable<HTMLNodeWrapper> {
	
	private Node node;
	
	/**
	 * Creates a new HTMLNodeWrapper.
	 * @param node is the DOM Node object
	 */
	public HTMLNodeWrapper(Node node) { 
		this.node = node; 
	}
	
	// ### Extra methods ###
	
	/**
	 * Gets attribute by name.
	 * @param attributeName is the name of the attribute
	 * @return wrapper of attribute, null if it doesn't exist
	 */
	public HTMLNodeWrapper getAttributeWrapper (String attributeName) {
		Node attribute = null;
		
		if (hasAttributes()) {
			attribute = getAttributes().getNamedItem(attributeName);
		}
		
		return attribute != null ? new HTMLNodeWrapper(attribute) : null;
	}

	/**
	 * Gets attribute value.
	 * It is safe, so it will always return
	 * a value that is not null.
	 * @param attributeName is the name of the attribute
	 * @return attribute value if it exists, empty string otherwise
	 */
	public String getSafeAttributeValue (String attributeName) {
		HTMLNodeWrapper attributeWrapper = getAttributeWrapper(attributeName);
		
		return attributeWrapper != null ? attributeWrapper.getSafeValue() : "";
	}
	
	/**
	 * Gets node list wrapper of child nodes.
	 * @return wrapped node list containing children
	 */
	public HTMLNodeListWrapper getChildrenNodesWrapper () {
		return new HTMLNodeListWrapper (getChildNodes());
	}
	
	/**
	 * Gets node list wrapper by name.
	 * @param name is the name of the node(s)
	 * @return wrapped node list containing nodes with name
	 */
	public HTMLNodeListWrapper getNodeListWrapperByName (String name) {
		ArrayList<Node> nodeList = new ArrayList<Node>();
		buildNodeList(nodeList, name);
		
		return new HTMLNodeListWrapper (nodeList);
	}
	
	/**
	 * Same as {@link #getNodeListWrapperByName(String)},
	 * but only adds node to list if it fulfills all the predicates
	 * passed.
	 * @param name is the name of the node(s)
	 * @param predicates is all the predicates that each node has to pass
	 * to be added to list
	 * @return wrapped node list containing nodes with name that have 
	 * passed all predicates
	 */
	public HTMLNodeListWrapper getNodeListWrapperByName (String name, HTMLPredicate ... predicates) {
		final ArrayList<Node> nodeList = new ArrayList<Node>();
		
		for (HTMLNodeWrapper node : getNodeListWrapperByName(name)) {
			boolean passedPredicates = true;
			
			for (HTMLPredicate predicate : predicates) {
				if (!predicate.match(node)) {
					passedPredicates = false;
					break;
				}
			}
			
			if(passedPredicates)
				nodeList.add(node);
		}
		
		return new HTMLNodeListWrapper (nodeList);
	}
	
	/**
	 * Gets the attribute called 'name' if it exists.
	 * @return 'name' attribute or null if it doesn't exist
	 */
	public String getNameAttribute () {
		HTMLNodeWrapper nameAttribute = getAttributeWrapper("name");
		
		return nameAttribute != null ? nameAttribute.getNodeValue() : null;
	}
	
	/**
	 * Gets the attribute called 'id' if it exists.
	 * @return 'id' attribute or null if it doesn't exist
	 */
	public String getIDAttribute () {
		HTMLNodeWrapper idAttribute = getAttributeWrapper("id");
		
		return idAttribute != null ? idAttribute.getNodeValue() : null;
	}
	
	/**
	 * Gets the attribute called 'value' if it exists.
	 * @return 'value' attribute or null if it doesn't exist
	 */
	public String getValueAttribute () {
		HTMLNodeWrapper valueAttribute = getAttributeWrapper("value");
		
		return valueAttribute != null ? valueAttribute.getNodeValue() : null;
	}
	
	/**
	 * Gets the (safe) node value.
	 * Will never return null.
	 * @return value of node if it exists, empty string otherwise
	 */
	public String getSafeValue () {
		return getNodeValue() != null ? getNodeValue() : "";
	}
	
	/**
	 * Generates full HTML string for this node.
	 * @return string containing HTML code
	 */
	public String generateHTML () {
		StringBuilder htmlBuilder = new StringBuilder();
		buildBody(htmlBuilder, 0);
		
		return htmlBuilder.toString();
	}
	
	/**
	 * Generates local HTML for this node only.
	 * Does not include ending tag.
	 * @return string containing local HTML code
	 */
	public String generateLocalHTML () {
		if (getNodeName().equalsIgnoreCase("#document")) {
			return "<!DOCTYPE html>";
		}
		
		if (getNodeName().equalsIgnoreCase("#text")) {
			return clean(getNodeValue()).trim();
		}
		
		StringBuilder htmlBuilder = new StringBuilder();
		
		htmlBuilder.append("<");
		htmlBuilder.append(clean(getNodeName()));
		
		if (hasAttributes()) {
			NamedNodeMap nodeMap = getAttributes();
			
			for (int i = 0; i < nodeMap.getLength(); i++) {
				htmlBuilder.append(" " + clean(nodeMap.item(i).toString()));
			}
		}
		
		if (!hasChildNodes())
			htmlBuilder.append(" /");
		
		htmlBuilder.append(">");
		
		return htmlBuilder.toString();
	}
	
	private void buildNodeList (ArrayList<Node> nodeList, String name) {
		if (getNodeName().equalsIgnoreCase(name)) {
			nodeList.add(this);
		}
		
		if (hasChildNodes()) {
			for (HTMLNodeWrapper child : getChildrenNodesWrapper()) {
				child.buildNodeList(nodeList, name);
			}
		}
	}
	
	protected void buildBody (StringBuilder builder, int level) {
		boolean isDocumentStart = getNodeName().equalsIgnoreCase("#document");
		
		if (getNodeName().equalsIgnoreCase("#text")) {
			if (clean(getNodeValue()).trim().isEmpty())
				return;
		}
		
		addIndentation(builder, level);
		builder.append(generateLocalHTML() + "\r\n");
		
		if (hasChildNodes()) {
			int newLevel = isDocumentStart ? level : level + 1;
			
			for (HTMLNodeWrapper child : getChildrenNodesWrapper()) {
				child.buildBody(builder, newLevel);
			}
			
			addIndentation(builder, level);
			
			if (!isDocumentStart)
				builder.append("</" + getNodeName() + ">\r\n");
		}
	}
	
	private void addIndentation (StringBuilder builder, int level) {
		for (int i = 0; i < level; i++) {
			builder.append("\t");
		}
	}
	
	private String clean (String text) {
		if (text == null)
			return "";
		
		return text.replace("\r", "").replace("\n", "");
	}
	
	// ### Interface imlementation ###
	
	@Override
	public Iterator<HTMLNodeWrapper> iterator() {
		return getChildrenNodesWrapper().iterator();
	}
	
	// ### Wrapper methods ###
	
	public Node appendChild(Node newChild) throws DOMException { return node.appendChild(newChild); }
	public Node cloneNode(boolean deep) { return node.cloneNode(deep); }
	public short compareDocumentPosition(Node other) throws DOMException { return compareDocumentPosition(other); }
	public NamedNodeMap getAttributes() { return node.getAttributes(); }
	public String getBaseURI() { return node.getBaseURI(); }
	public NodeList getChildNodes() { return node.getChildNodes(); }
	public Object getFeature(String feature, String version) { return node.getFeature(feature, version); }
	public Node getFirstChild() { return node.getFirstChild(); }
	public Node getLastChild() { return node.getLastChild(); }
	public String getLocalName() { return node.getLocalName(); }
	public String getNamespaceURI() { return node.getNamespaceURI(); }
	public Node getNextSibling() { return node.getNextSibling(); }
	public String getNodeName() { return node.getNodeName(); }
	public short getNodeType() { return node.getNodeType(); }
	public String getNodeValue() throws DOMException { return node.getNodeValue(); }
	public Document getOwnerDocument() { return node.getOwnerDocument(); }
	public Node getParentNode() { return node.getParentNode(); }
	public String getPrefix() { return node.getPrefix(); }
	public Node getPreviousSibling() { return node.getPreviousSibling(); }
	public String getTextContent() throws DOMException { return node.getTextContent(); }
	public Object getUserData(String key) { return node.getUserData(key); }
	public boolean hasAttributes() { return node.hasAttributes(); }
	public boolean hasChildNodes() { return node.hasChildNodes(); }
	public Node insertBefore(Node newChild, Node refChild) throws DOMException { return node.insertBefore(newChild, refChild); }
	public boolean isDefaultNamespace(String namespaceURI) { return node.isDefaultNamespace(namespaceURI); }
	public boolean isEqualNode(Node arg) { return node.isEqualNode(arg); }
	public boolean isSameNode(Node other) { return node.isSameNode(other); }
	public boolean isSupported(String feature, String version) { return node.isSupported(feature, version); }
	public String lookupNamespaceURI(String prefix) { return node.lookupNamespaceURI(prefix); }
	public String lookupPrefix(String namespaceURI) { return node.lookupPrefix(namespaceURI); }
	public void normalize() { node.normalize(); }
	public Node removeChild(Node oldChild) throws DOMException { return node.removeChild(oldChild); }
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException { return node.replaceChild(newChild, oldChild); }
	public void setNodeValue(String nodeValue) throws DOMException { node.setNodeValue(nodeValue); }
	public void setPrefix(String prefix) throws DOMException { node.setPrefix(prefix); }
	public void setTextContent(String textContent) throws DOMException { node.setTextContent(textContent); }
	public Object setUserData(String key, Object data, UserDataHandler handler) { return node.setUserData(key, data, handler); }
	public String toString () { return node.toString(); }
	
}
