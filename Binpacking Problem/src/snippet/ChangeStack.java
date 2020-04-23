package snippet;

import java.util.List;

/**
 * A ChangeStack should have a List<List<Changes>> or similar.
 * It is an item that can change itself take in and apply a list of changes 
 * to itself as a "stack frame" and then revert those changes when that frame 
 * is popped.
 * 
 * @author Andrew Wock
 *
 */
public interface ChangeStack<T> {
	
	/**
	 * Create a new frame of changes.
	 */
	public void newChangeFrame();
	
	/**
	 * Push a new change without creating a new frame.
	 * @param change The change to apply.
	 */
	public void pushChange(Change<T> change);

	/**
	 * Apply and store the next stack frame, a list of changes.
	 * 
	 * @param changes The list of changes in the frame.
	 */
	public void pushChangeFrame(List<Change<T>> changes);
	
	/**
	 * Revert the changes in the top stack frame.
	 */
	public void popChangeFrame();
	
}
