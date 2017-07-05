/**
 * An exception that occurs when trying to get a bicycle from an empty position
 * of belt
 */
public class NoBicycleException extends HandlingException {

	/**
	 * Create a new NoBicycleException
	 */
	public NoBicycleException(String message) {
		super(message);
	}

}
