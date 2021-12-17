package bgu.spl.mics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


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
	private static Object lockBtAwaitSen = new Object();


	//Constructor
	private MessageBusImpl(){ //check if this is a method
		events_MS = new HashMap<Class<? extends Event>, BlockingQueue<MicroService>>();
		broadcasts_MS = new HashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>>();
		microservice_queues = new HashMap<MicroService, BlockingQueue<Message>>();
		futureOfEvent = new HashMap<Event, Future>();
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
		boolean found = false;
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
			boolean found = false;
			if( !broadcasts_MS.containsKey(type)){
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
	 * creates a new MS->creates a new queue for him and add him into the hash of MS_queue
	 * @param m the microservice to create a queue for.
	 * @pre:  None
	 * @post: microservice_queue.containsKey(m) == true
	 */
	@Override //->tick reg only after this register
	public void register(MicroService m) {
		synchronized (this){
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
			synchronized (this) {
				microservice_queues.remove(m);
				//System.out.println(microservice_queues.get(m));
				//events
				for(BlockingQueue block:events_MS.values()){
					if(block!=null)
						block.remove(m);
				}
				//while(events_MS.values().remove(m));
				//broadcast
				for(BlockingQueue block:broadcasts_MS.values()){
					if(block!=null)
						block.remove(m);
				}
				//while(broadcasts_MS.values().remove(m));
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
		synchronized (lockBtAwaitSen) {
			if (!broadcasts_MS.isEmpty()) {
				if (broadcasts_MS.get(b.getClass()) != null) {
					BlockingQueue<MicroService> microServicesOFb = broadcasts_MS.get(b.getClass());
					for (MicroService ms : microServicesOFb) {
						BlockingQueue<Message> help = microservice_queues.get(ms);
//					if(help==null)
//						System.out.println(ms.getName());
						help.add(b);
						microservice_queues.put(ms, help);
					}
					synchronized (this) {
						this.notifyAll();
					}
				}
			}
		}

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
		if (!events_MS.isEmpty()) {
			futureOfEvent.put(e, ev_future);
			if (events_MS.get(e.getClass()) != null) {
				synchronized (events_MS.get(e.getClass())) {
					BlockingQueue<MicroService> microServicesOFe = events_MS.get(e.getClass());
					MicroService first = microServicesOFe.poll();
					if (first != null) {
						BlockingQueue<Message> first_Messages = microservice_queues.get(first);
						first_Messages.add(e);
						microservice_queues.put(first, first_Messages);
						microServicesOFe.add(first);
						events_MS.put(e.getClass(), microServicesOFe);
					}
				}
			}
			synchronized (this) {
				this.notifyAll();
			}
		}
		return ev_future;
	}

	/**
	 *
	 *
	 * @param m The microservice requesting to take a message from its message queue.
	 *
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microservice_queues.get(m).take();
	}
		/*
//		BlockingQueue<Message> ms = microservice_queues.get(m);
//		synchronized (microservice_queues.get(m)) {
//			while (ms.isEmpty()) {
//				try {
//					microservice_queues.get(m).wait();
//				} catch (InterruptedException e) {
//					System.out.
//
//					println("InterruptedException");
//				}
//			}
//			return (microservice_queues.get(m).poll());
//		}
	}*/


	/**
	 *
	 * check that m is subscribed to type in hashMap of event
	 * @return True or False
	 */
	public <T> boolean microserviceInEvents(Class<? extends Event<T>> type, MicroService m){
		BlockingQueue<MicroService> microServicesOFe= events_MS.get(type);
		return (microServicesOFe.contains(m));
	}

	/**
	 *
	 * check that m is subscribed to type in hashMap of broadcast
	 * @return True or False
	 */
	public boolean microserviceInBroadcasts(Class<? extends Broadcast> type, MicroService m){
		BlockingQueue<MicroService> microServicesOFb= broadcasts_MS.get(type);
		return (microServicesOFb.contains(m));
	}

	/**
	 *
	 * return if the future is being resolved
	 * @param e the event that we check if we complete
	 * @param result the result we have
	 * @return Boolean- true or false
	 */
	public <T> boolean isComplete(Event<T> e, T result){
		Future<T> future = futureOfEvent.get(e);
		return (future != null);
	}

	/**
	 *
	 * check that microservice got the broadcast
	 * @return Boolean- true or false
	 */
	public boolean sucSendBroadcast(Message b, MicroService m){
		boolean found = false;
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
	 * @return Boolean- true or false
	 */
	public boolean sucSendEvent(Message e, MicroService m){
		boolean found = false;
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
