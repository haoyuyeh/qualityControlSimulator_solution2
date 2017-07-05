/**
 * The driver of the simulation
 */

public class Sim {
	/**
	 * Create all components and start all of the threads.
	 */
	public static void main(String[] args) {

		/*
		 * create a main part of the system,including a belt, a producer, and a
		 * consumer, to transfer all incoming bicycles
		 */
		Belt mainBelt = new Belt("mainBelt", 5, true);
		Producer producer = new Producer(mainBelt);
		Consumer consumer1 = new Consumer(mainBelt);
		BeltMover mainBeltMover = new BeltMover(mainBelt);

		/*
		 * create a second part of the system,including a belt, an inspector,
		 * and a consumer, to examine all tagged bicycles and send inspected
		 * bicycles to second belt
		 */
		Belt inspectorBelt = new Belt("inspectorBelt", 2);
		Consumer consumer2 = new Consumer(inspectorBelt);
		BeltMover inspectorBeltMover = new BeltMover(inspectorBelt);
		Inspector inspector = new Inspector(mainBelt, inspectorBelt);

		consumer1.start();
		producer.start();
		mainBeltMover.start();
		consumer2.start();
		inspectorBeltMover.start();
		inspector.start();

		while (consumer1.isAlive() && producer.isAlive()
				&& mainBeltMover.isAlive() && consumer2.isAlive()
				&& inspectorBeltMover.isAlive() && inspector.isAlive())
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				BicycleHandlingThread.terminate(e);
			}

		// interrupt other threads
		consumer1.interrupt();
		producer.interrupt();
		mainBeltMover.interrupt();
		consumer2.interrupt();
		inspectorBeltMover.interrupt();
		inspector.interrupt();

		System.out.println("Sim terminating");
		System.out.println(BicycleHandlingThread.getTerminateException());
		System.exit(0);
	}
}
