/**
 * The bicycle quality control belt
 */
public class Belt {

	// the items in the belt segments
	protected Bicycle[] segment;

	// the length of this belt
	protected int beltLength;

	// the name of this belt
	protected String beltName;

	// decide whether a belt will have a sensor
	protected boolean haveSensor;

	// to help format output trace
	final private static String indentation = "                              ";

	/**
	 * Create a new, empty belt without sensor, initialised as empty
	 */
	public Belt(String beltName, int beltLength) {
		this.beltName = beltName;
		this.beltLength = beltLength;
		this.haveSensor = false;
		segment = new Bicycle[beltLength];
		for (int i = 0; i < segment.length; i++) {
			segment[i] = null;
		}
	}

	/**
	 * Create a new, empty belt with sensor, initialised as empty
	 */
	public Belt(String beltName, int beltLength, boolean haveSensor) {
		this.beltName = beltName;
		this.beltLength = beltLength;
		this.haveSensor = haveSensor;
		segment = new Bicycle[beltLength];
		for (int i = 0; i < segment.length; i++) {
			segment[i] = null;
		}
	}

	/**
	 * Put a bicycle on the belt.
	 * 
	 * @param bicycle
	 *            the bicycle to put onto the belt.
	 * @param index
	 *            the place to put the bicycle
	 * @param message
	 *            a note of event
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void put(Bicycle bicycle, int index, String message)
			throws InterruptedException {

		// while there is another bicycle in the way, block this thread
		while (segment[index] != null) {
			wait();
		}

		// insert the element at the specified location
		segment[index] = bicycle;

		// make a note of the event in output trace
		System.out.println(message);

		// notify any waiting threads that the belt has changed
		notifyAll();
	}

	/**
	 * get a bicycle on the belt.
	 *
	 * @param index
	 *            the place to get the bicycle
	 * @param message
	 *            a note of event
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 * @throws NoBicycleException
	 *             if trying to get a bicycle from an empty position of belt
	 */
	public synchronized Bicycle get(int index, String message)
			throws InterruptedException, NoBicycleException {

		Bicycle bicycle;

		// while there is no bicycle at the specific position of the belt, block
		// this thread
		while (segment[index] == null) {
			String eMessage = "there is no bicycle at position " + index;
			throw new NoBicycleException(eMessage);
		}

		// get the item
		bicycle = segment[index];
		segment[index] = null;
		// make a note of the event in output trace
		System.out.println(indentation + indentation + bicycle
				+ " is removed from " + beltName);
		System.out.println(indentation + indentation + bicycle + message);

		// notify any waiting threads that the belt has changed
		notifyAll();
		return bicycle;
	}

	/**
	 * Take a bicycle off the end of the belt
	 * 
	 * @return the removed bicycle
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 * @throws DefException
	 *             if a defective bicycle makes it to the end of the belt
	 *             without being inspected
	 */
	public synchronized Bicycle getEndBelt()
			throws InterruptedException, DefException {

		Bicycle bicycle;

		// while there is no bicycle at the end of the belt, block this thread
		while (segment[segment.length - 1] == null) {
			wait();
		}

		// get the next item
		bicycle = segment[segment.length - 1];
		// check whether the defective bicycle located at the end of the belt is
		// inspected
		if (bicycle.isDefective() && !bicycle.isInspected()) {
			String message = "A defective bicycle is not inspected";
			throw new DefException(message);
		} else {
			segment[segment.length - 1] = null;
			// make a note of the event in output trace
			System.out.println(indentation + indentation + bicycle
					+ " departed from " + beltName);

			// notify any waiting threads that the belt has changed
			notifyAll();
			return bicycle;
		}
	}

	/**
	 * Move the belt along one segment
	 * 
	 * @throws OverloadException
	 *             if there is a bicycle at position beltLength.
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void move()
			throws InterruptedException, OverloadException {

		if (haveSensor) {
			// if there is something at the end of the belt,
			// or the belt is empty,
			// or segment 3 is occupied by a bike which is tagged and not
			// inspected,
			// do not move the belt
			while (isEmpty() || sensor()
					|| segment[segment.length - 1] != null) {
				wait();
			}
		} else {
			// if there is something at the end of the belt,
			// or the belt is empty,
			// do not move the belt
			while (isEmpty() || segment[segment.length - 1] != null) {
				wait();
			}
		}

		// double check that a bicycle cannot fall of the end
		if (segment[segment.length - 1] != null) {
			String message = "Bicycle fell off end of " + " belt";
			throw new OverloadException(message);
		}

		// move the elements along, making position 0 null
		for (int i = segment.length - 1; i > 0; i--) {
			if (this.segment[i - 1] != null) {
				System.out.println(indentation + this.segment[i - 1] + " [ s"
						+ (i) + " -> s" + (i + 1) + " ]");
			}
			segment[i] = segment[i - 1];
		}
		segment[0] = null;

		// notify any waiting threads that the belt has changed
		notifyAll();
	}

	/**
	 * sensoring the segment 3 to check whether it is occupied. if it is
	 * occupied, checking whether it is tagged and whether it is inspected
	 * 
	 * @return true means segment 3 is occupied by a bike which is tagged and
	 *         not inspected.
	 */
	public boolean sensor() {
		if (segment[2] != null) {
			if (segment[2].isTagged() && !segment[2].isInspected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the maximum size of this belt
	 */
	public int length() {
		return beltLength;
	}

	/**
	 * Peek at what is at a specified segment
	 * 
	 * @param index
	 *            the index at which to peek
	 * @return the bicycle in the segment (or null if the segment is empty)
	 */
	public Bicycle peek(int index) {
		Bicycle result = null;
		if (index >= 0 && index < beltLength) {
			result = segment[index];
		}
		return result;
	}

	/**
	 * Check whether the belt is currently empty
	 * 
	 * @return true if the belt is currently empty, otherwise false
	 */
	private boolean isEmpty() {
		for (int i = 0; i < segment.length; i++) {
			if (segment[i] != null) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		return java.util.Arrays.toString(segment);
	}

	/*
	 * @return the final position on the belt
	 */
	public int getEndPos() {
		return beltLength - 1;
	}
}
