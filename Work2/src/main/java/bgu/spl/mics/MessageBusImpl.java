package bgu.spl.mics;
import bgu.spl.mics.application.objects.Cluster;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
// to all MS we need to write a callback
//Callback <Type message-class> nameofcallback = input name(going to be type of message-Broad or Eve) -> {
//body of callback
//};


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//Fields
	private HashMap<Class<? extends Event>, BlockingQueue<MicroService>> events_MS;
	private HashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcasts_MS;
	private HashMap<MicroService, BlockingQueue<Message>> microservice_queues;
	private HashMap<Event, Future> futureOfEvent;
	private static MessageBus thisMB = null;
	private static Object micro_queue = new Object();


	//Constructor
	private MessageBusImpl(){ //check if this is a method
		events_MS = new HashMap();
		broadcasts_MS = new HashMap();
		microservice_queues = new HashMap();
		futureOfEvent = new HashMap();
	}

	/**
	 *
	 * Retrieves the single instance of this class.
	 */
	public static synchronized MessageBus getInstance() {
		if(thisMB == null){
			thisMB = new MessageBusImpl();
		}
		return thisMB;
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
		BlockingQueue<MicroService> queueMS;
		Boolean found = false;
		if(!events_MS.containsKey(type)){
			queueMS = new LinkedBlockingQueue<MicroService>();
		}
		else{ //type exists
			queueMS = events_MS.get(type); //get()->return the value of this key
			//check if MS is already subscribed
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
			BlockingQueue<MicroService> queueMS;
			Boolean found = false;
			if(!broadcasts_MS.containsKey(type)){
				queueMS = new LinkedBlockingQueue<MicroService>();
			}
			else{ //type exists
				queueMS = broadcasts_MS.get(type); //get()->return the value of this key
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
			Future<T> res = futureOfEvent.get(e);
			res.resolve(result);
			futureOfEvent.put(e,res);
		}
	}

	/**
	 *
	 * creates a new MS->creats a new queue for him and add him into the hash of MS_queue
	 * @param m the microservice to create a queue for.
	 * @pre:  None
	 * @post: microservice_queue.containsKey(m) == true
	 */
	@Override //->tick reg only after this register
	public void register(MicroService m) {
		synchronized (micro_queue){
			BlockingQueue<Message> mes = new LinkedBlockingQueue <Message>();
			microservice_queues.put(m, mes);
		}
	}

	/**
	 *
	 * @param m the microservice to unregister.
	 * if microservice.containsKey(m) == false then no need to do anything
	 */
	@Override
	public void unregister(MicroService m) {
		if(microservice_queues.containsKey(m)){ //registered
			m.terminate();
				//remove from all the hash maps
				//microservice
			synchronized (micro_queue) {
				microservice_queues.remove(m);
				//events
				while(events_MS.values().remove(m));
				//broadcast
				while(broadcasts_MS.values().remove(m));
			}
		}
	}

	/**
	 *
	 * a. take all the MS that subscribed to b broadcast
	 * b. push the broadcast to all the MS that we got
	 * @param b 	The message to added to the queues.
	 *
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> microServicesOFb= broadcasts_MS.get(b.getClass());
		for(MicroService ms:microServicesOFb){
			microservice_queues.get(ms).add(b);
		}
		notifyAll();
	}

	/**
	 *
	 * a. create a future for this event + enter this to the future hash
	 * b. take all the MS that sub to e event and assign in Round-Robin method
	 * @param e     	The event to add to the queue.
	 * @return Future<T> to the student
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> ev_future = new Future<T>();
		futureOfEvent.put(e, ev_future);
		BlockingQueue<MicroService> microServicesOFe= events_MS.get(e.getClass());
		MicroService first = microServicesOFe.poll();
		if(first != null){
			microservice_queues.get(first).add(e);
			microServicesOFe.add(first);
		}
		notifyAll();
		return ev_future;
	}

	/**
	 *
	 *
	 * @param m The microservice requesting to take a message from its message queue.
	 *
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> ms = microservice_queues.get(m);
		synchronized (microservice_queues) {
			while (ms.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					System.out.println("InterruptedException");
				}
			}
			return null;
		}
	}


	/**
	 *
	 * check that m is subscribed to type in hashMap of event
	 * @param type
	 * @param m
	 * @return True or False
	 */
	public <T> boolean microserviceInEvents(Class<? extends Event<T>> type, MicroService m){
		BlockingQueue<MicroService> microServicesOFe= events_MS.get(type);
		return (microServicesOFe.contains(m));
	}

	/**
	 *
	 * check that m is subscribed to type in hashMap of broadcast
	 * @param type
	 * @param m
	 * @return True or False
	 */
	public boolean microserviceInBroadcasts(Class<? extends Broadcast> type, MicroService m){
		BlockingQueue<MicroService> microServicesOFb= broadcasts_MS.get(type);
		return (microServicesOFb.contains(m));
	}

	/**
	 *
	 * return if the future is being resolved
	 * @param e the event that we check if we compelte
	 * @param result the result we have
	 * @param <T>
	 * @return Boolean- true or false
	 */
	public <T> boolean isComplete(Event<T> e, T result){
		Future<T> future = futureOfEvent.get(e);
		return (future != null);
	}

	/**
	 *
	 * check that microservice got the broadcast
	 * @param b
	 * @param m
	 * @return Boolean- true or false
	 */
	public boolean sucSendBroadcast(Message b, MicroService m){
		Boolean found = false;
		BlockingQueue<Message> microserviceB = microservice_queues.get(m);
		for(Message ms: microserviceB){
			if(ms == b){
				found = true;
			}
		}
		return found;
	}

	/**
	 *
	 * check that microservice got the event
	 * @param e
	 * @param m
	 * @return Boolean- true or false
	 */
	public boolean sucSendEvent(Message e, MicroService m){
		Boolean found = false;
		BlockingQueue<Message> microserviceE = microservice_queues.get(m);
		for(Message ms: microserviceE){
			if(ms == e){
				found = true;
			}
		}
		return found;
	}

	/**
	 *
	 * check if Microservice registered and has a queue
	 * @param m the microService that will register
	 * @return Boolean - true, false
	 */
	public boolean isMicroServiceRegistered(MicroService m){
		return (microservice_queues.containsKey(m));
	}

	/**
	 *
	 *
	 * @param m the microService we want tp get massage from him
	 * @return Boolean - true, false
	 */
	public boolean hasAwaitMassage(MicroService m){
		return  true;
	}

}
