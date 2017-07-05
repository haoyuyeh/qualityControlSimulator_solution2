/**
 * An exception that occurs when the inspector is asked to inspect a bicycle
 * which is not tagged as defective
 */
public class DefKnownException extends HandlingException {
	/**
	 * Create a new UnsusException
	 */
	public DefKnownException(String message) {
		super(message);
	}
}
