package rubenchristoffer.hetty.misc;

/**
 * Interface used for providing a method
 * to deep clone objects.
 * @author Ruben Christoffer
 * @param <T> is the type you want cloned object to be
 */
public interface DeepCloneable<T> {

	/**
	 * Creates a deep clone of this instance.
	 * That means that this object and any possible
	 * sub-objects have unique values.
	 * @return Deep-cloned object
	 */
	public T cloneDeep();
	
}
