package bgu.spl.mics.application.services;

import bgu.spl.mics.*;

import java.util.Timer;
import java.util.TimerTask;

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
	private Timer timer;
	private int speed;
	private int duration;
	private int currentTime = 0;
	MessageBus messageBus = MessageBusImpl.getInstance();

	public TimeService(int speed, int duration) {
		super("timeService");
		this.speed = speed;
		this.duration = duration;
		timer= new Timer();
	}

	/**
	 * this function send the broadCast if we pass the duration
	 * we build TickBroadcast with true parameter which mean that we need to terminate the service
	 * otherwise we construct it with false parameter which it means to do regular tick
	 */
	private void broadcastTick(){
		System.out.println(currentTime);
		currentTime++;
		Broadcast tickBroadcast;
		if(currentTime == duration) {
			sendBroadcast(new Terminated());
			timer.cancel();
		}
		else {
			tickBroadcast = new TickBroadcast();
			messageBus.sendBroadcast(tickBroadcast);
		}

	}

	@Override
	protected void initialize() {
		subscribeBroadcast(Terminated.class, (terminate) -> this.terminate());
		TimerTask task = new TimerTask() {
			public void run() {
				broadcastTick();
			}
		};
		timer.schedule(task,1,speed);
	}
}
