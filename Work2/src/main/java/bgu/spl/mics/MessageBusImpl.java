package bgu.spl.mics;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//Fields
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> events_MS;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcasts_MS;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microservice_queues;
	private ConcurrentHashMap<Event, Future> future;
	private static Object lockMBQ = new Object();

	//Constructor
	public MessageBusImpl(){ //check if this is a method
		events_MS = new ConcurrentHashMap();
		broadcasts_MS = new ConcurrentHashMap();
		microservice_queues = new ConcurrentHashMap();
		future = new ConcurrentHashMap();
	}

	/**
	 *
	 * @param type The type to subscribe to,
	 * @param m    The subscribing microservice.
	 * add a microservice to the value(=vector of MS) of a type of Message(=key) in the hush
	 * @pre: microservice.containsKey(m) == true ->m is registered
	 *
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		ConcurrentLinkedQueue<MicroService> queueMS;
		Boolean found = false;
		if(!events_MS.containsKey(type)){
			queueMS = new ConcurrentLinkedQueue<MicroService>();
		}
		else{ //type exists
			queueMS = (ConcurrentLinkedQueue<MicroService>) events_MS.get(type); //get()->return the value of this key
			for(MicroService ms: queueMS){
				if(ms == m){
					found = true; //I do not want to enter m twice
				}
			}
		}
		if(!found){
			queueMS.add(m);
			events_MS.put(type,queueMS);//put() -> if type exists it will only change the value of the key
			                            //         else create a new one and add the key and the value
		}
	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing microservice.
	 * add a microservice to the value(=vector of MS) of a type of Broadcast(=key) in the hush
	 * @pre: microservice.containsKey(m) == true ->m is registered
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(microservice_queues.containsKey(m)){ // m is registered
			ConcurrentLinkedQueue<MicroService> queueMS;
			Boolean found = false;
			if(!broadcasts_MS.containsKey(type)){
				queueMS = new ConcurrentLinkedQueue<MicroService>();
			}
			else{ //type exists
				queueMS = (ConcurrentLinkedQueue<MicroService>) broadcasts_MS.get(type); //get()->return the value of this key
				for(MicroService ms: queueMS){
					if(ms == m){
						found = true; //I do not want to enter m twice
					}
				}
			}
			if(!found){
				queueMS.add(m);
				broadcasts_MS.put(type,queueMS); //put() -> if type exists it will only change the value of the key
				                                 //         else create a new one and add the key and the value
			}
		}

	}

	/**
	 *
	 * find the right future event of this e event and resolve it with function from Future
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * call the function of Event that will call the function of Future resolve
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		if(result != null){
			if(future.containsKey(e)){ //if this event exists
				Future<T> res = future.get(e);
				res.resolve(result);
				future.put(e,res);
			}
		}
	}

	/**
	 *
	 * creates a new MS->creats a new queue for him and add him into the hash of MS_queue
	 * @param m the microservice to create a queue for.
	 * @pre:  None
	 * @post: microservice_queue.containsKey(m) == true
	 */
	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> mes = new LinkedBlockingQueue <Message>();
		synchronized(lockMBQ){
			microservice_queues.put(m, mes); //key = m ; value = mes
		}
	}

	/**
	 *
	 * @param m the microservice to unregister.
	 * if microservice.containsKey(m) == false then no need to do anything
	 */
	@Override
	public void unregister(MicroService m) { //synchronised
		if(microservice_queues.containsKey(m)){ //registered
			//???????????????????????????????????????
			//remove from all the hash maps
			//microservice
			microservice_queues.remove(m);
			//events
			events_MS.values().remove(m);
			//broadcast
			broadcasts_MS.values().remove(m);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * a. create a future for this event + enter this to the future hash
	 * b.
	 * @param e     	The event to add to the queue.
	 * @return Future<T> to the student
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> ev_future = new Future<T>();
		future.put(e, ev_future);
		return null;
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}


	//private RoundRobin(){}




	/**
	 * function of type getter in order to get the info we need from outside classes
	 */
	public <T> boolean microserviceInEvents(Class<? extends Event<T>> type, MicroService m){
		return true;
	}
	public boolean microserviceInBroadcasts(Class<? extends Broadcast> type, MicroService m){
		return true;
	}
	public <T> boolean isComplete(Event<T> e, T result){return true;}
	public boolean sucSendBroadcast(MicroService m){return true;}
	public boolean sucSendEvent(MicroService m){return true;}
	public boolean isMicroServiceRegistered(MicroService m){return true;}
	public boolean hasAwaitMassage(MicroService m){return  true;}

}
