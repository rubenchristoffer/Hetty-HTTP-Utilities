package io.github.rubenchristoffer.hetty.html;

/**
 * Interface for a HTML predicate.
 * This is a condition that has to be true
 * for a given node. 
 * @author Ruben Christoffer
 */
public interface HTMLPredicate {
	
	/**
	 * Is the condition based on node true?
	 * @param predicate is the node you want to check for
	 * @return true if predicate is true, false otherwise
	 */
	public boolean match(HTMLNodeWrapper predicate);
	
}
