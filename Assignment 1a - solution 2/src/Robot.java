/*
 * the robot used to carry bicycle between belts and inspector. initial position
 * of robot's arm is located at belt
 */
public class Robot extends BicycleHandlingThread {
	// the belt contains all bicycles including tagged ones
	protected Belt mainBelt;
	// the belt contains bicycles which have been inspected
	protected Belt inspectorBelt;

	/*
	 * create a robot to get a bicycle from main belt or return a inspected
	 * bicycle to the inspector belt
	 */
	public Robot(Belt mainBelt, Belt inspectorBelt) {
		super();
		this.mainBelt = mainBelt;
		this.inspectorBelt = inspectorBelt;
	}

	/**
	 * Take a bicycle from segment 3 of the main belt to inspector
	 * 
	 * @return the bicycle in segment 3
	 */
	public Bicycle fetchBicycle() {
		Bicycle temp = null;
		try {
			temp = mainBelt.get(2, " sended to inspector");
			sleep(Params.ROBOT_MOVE_TIME);
		} catch (NoBicycleException e) {
			terminate(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * Put a inspected bicycle on inspector belt.
	 * 
	 * @param bicycle
	 *            the bicycle to put onto the belt.
	 */
	public void returnBicycle(Bicycle bicycle) {
		try {
			sleep(Params.ROBOT_MOVE_TIME);
			String message = bicycle + " arrived on inspector belt";
			inspectorBelt.put(bicycle, 0, message);

			// move arm from second belt to inspector and then to main belt
			sleep(2 * Params.ROBOT_MOVE_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
