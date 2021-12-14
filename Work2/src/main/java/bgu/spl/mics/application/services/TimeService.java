package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import java.util.Timer;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	// current timer
	// end

	//Fields
	private int speed;
	private int duration;
	private int currentTime = 0;

	public TimeService(int speed, int duration) {
		super("timeService");
		this.speed = speed;
		this.duration = duration;
	}
	//tick()
	private void broadcastTick(){
		currentTime++;
		//send broadcast of the updated time
	}

	/**
	 * this function need to terminate all the event that are still running at the program
	 */
	private void terminateAll(){

	}
	@Override
	protected void initialize() {
		// Timer
		//TimerTask(tick())
		//timer.(timertask)

		//Timer
	}

}
