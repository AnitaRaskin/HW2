package bgu.spl.mics;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private Hashtable events = new Hashtable();
	private Hashtable Broadcasts = new Hashtable();
	private Hashtable microserivce = new Hashtable();

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> boolean microserviceInEvents(Class<? extends Event<T>> type, MicroService m){
		return true;
	}

	/**
	 * need to finsh in the future
	 * @param type
	 * @param m
	 * @return
	 */
	public boolean microserviceInBroadcasts(Class<? extends Broadcast> type, MicroService m){
		return true;
	}

	/**
	 * this function give the future object that associated with the event
	 * @param result
	 * @param e
	 * @return
	 */
	 public <T> boolean isComplete(Event<T> e, T result){return true;}
	 public boolean sucSendBroadcast(MicroService m){return true;}
	 public boolean sucSendEvent(MicroService m){return true;}
	 public boolean isMicroServiceRegistered(MicroService m){return true;}
	 public boolean hasAwaitMassage(MicroService m){return  true;}

}
