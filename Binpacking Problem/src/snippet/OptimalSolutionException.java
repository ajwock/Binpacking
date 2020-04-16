package snippet;

/**
 * Exception used to indicate that an optimal solution has been detected.
 * 
 * The most obvious example of this is when an upper bound and a lower bound
 * used to optimize a problem are found to be equal- i.e. the optimal solution
 * can be no better or worse than the current instance.
 * 
 * @author Andrew Wock
 *
 */
public class OptimalSolutionException extends Exception {

	
	public OptimalSolutionException(Node node) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
