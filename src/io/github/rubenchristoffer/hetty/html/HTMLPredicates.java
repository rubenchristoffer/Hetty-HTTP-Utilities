package io.github.rubenchristoffer.hetty.html;

/**
 * Utility class used for easily
 * creating basic HTML predicates.
 * @author Ruben Christoffer
 */
public final class HTMLPredicates {

	/**
	 * Does the node have the provided attribute?
	 * @param attribute is the attribute you want to check if node has
	 * @return a HTML predicate generated based on parameter
	 */
	public static final HTMLPredicate HAS_ATTRIBUTE (final String attribute) {
		return new HTMLPredicate() {
			@Override
			public boolean match(HTMLNodeWrapper predicate) {
				return predicate.hasAttributes() && predicate.getAttributeWrapper(attribute) != null;
			}
		};
	}
	
	/**
	 * Same as {@link #HAS_ATTRIBUTE_VALUE(String, String, boolean)} 
	 * where ignoreCase=true
	 * @param attribute is the attribute you want to check if node has
	 * @param attributeValue is the value of the attribute that you want to check exists
	 * @return a HTML predicate generated based on parameters
	 */
	public static final HTMLPredicate HAS_ATTRIBUTE_VALUE (final String attribute, final String attributeValue) {
		return HAS_ATTRIBUTE_VALUE(attribute, attributeValue, true);
	}
	
	/**
	 * Does the node have the provided attribute and attribute value?
	 * @param attribute is the attribute you want to check if node has
	 * @param attributeValue is the value of the attribute that you want to check exists
	 * @param ignoreCase determines whether case should be ignored when checking attributeValue
	 * @return a HTML predicate generated based on parameters
	 */
	public static final HTMLPredicate HAS_ATTRIBUTE_VALUE (final String attribute, final String attributeValue, final boolean ignoreCase) {
		return new HTMLPredicate() {
			@Override
			public boolean match(HTMLNodeWrapper predicate) {
				if (predicate.hasAttributes()) {
					HTMLNodeWrapper attributeWrapper = predicate.getAttributeWrapper(attribute);
					
					if (attributeWrapper != null) {
						String value = attributeWrapper.getNodeValue();
						
						if (value != null) {
							if (ignoreCase)
								return value.equalsIgnoreCase(attributeValue);
							else
								return value.equals(attributeValue);
						}
					}
				}
				
				return false;
			}
		};
	}
	
}
