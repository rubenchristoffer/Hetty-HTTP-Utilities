package rubenchristoffer.hetty.html;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class that acts as a wrapper for a HTML Node List.
 * This means that all methods are being handled 
 * by the underlying NodeList interface (this class doesn't actually implement
 * any interface methods itself).
 * It also contains a few additional helper functions that are not part of the interface.
 * @author Ruben Christoffer
 */
public class HTMLNodeListWrapper implements NodeList, Iterable<HTMLNodeWrapper> {

	private NodeList nodeList;
	
	/**
	 * Creates a wrapper for the given HTML node list
	 * @param nodeList is a DOM node list
	 */
	public HTMLNodeListWrapper(NodeList nodeList) {
		this.nodeList = nodeList;
	}
	
	/**
	 * Creates a wrapper for the given node list.
	 * @param nodeList is a list of Node objects
	 */
	public HTMLNodeListWrapper (final List<Node> nodeList) {
		this.nodeList = new NodeList() {
			@Override
			public Node item(int index) {
				return nodeList.get(index);
			}
			
			@Override
			public int getLength() {
				return nodeList.size();
			}
		};
	}
	
	// ### Extra methods ###
	
	/**
	 * Gets the first node (as wrapper) in this list.
	 * @return HTMLNodeWrapper of first item in list
	 */
	public HTMLNodeWrapper first () {
		return new HTMLNodeWrapper (item(0));
	}
	
	/**
	 * Prints all nodes in list to standard output stream
	 */
	public void printAll () {
		Iterator<HTMLNodeWrapper> iterator = iterator();
		
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
	
	// ### Interface implementation ###
	
	@Override
	public Iterator<HTMLNodeWrapper> iterator() {
		return new Iterator<HTMLNodeWrapper>() {
			
			private int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < getLength();
			}

			@Override
			public HTMLNodeWrapper next() {
				index++;
				return new HTMLNodeWrapper (item(index - 1));
			}
			
		};
	}
	
	// ### Wrapper methods ###
	
	public int getLength() { return nodeList.getLength(); }
	public Node item(int index) { return nodeList.item(index); }
	public String toString () { return nodeList.toString(); }

}
