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
	private HashMap<Class<? extends Event>, BlockingQueue<MicroService>> hashMapEventWithMicroServices;
	private HashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> hashMapBroadcastWithMicroServices;
	private HashMap<MicroService, BlockingQueue<Message>> hashMapMicroserviceWithMessage;
	private HashMap<Event, Future> futureOfEvent;
	private static MessageBus thisMB = null;
	private static Object micro_queue = new Object();
	private static Object lockBtAwaitSen = new Object();


	//Constructor
	private MessageBusImpl(){ //check if this is a method
		hashMapEventWithMicroServices = new HashMap<Class<? extends Event>, BlockingQueue<MicroService>>();
		hashMapBroadcastWithMicroServices = new HashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>>();
		hashMapMicroserviceWithMessage = new HashMap<MicroService, BlockingQueue<Message>>();
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
		BlockingQueue<MicroService> microServicesOfEvent;
		boolean found = false;
		if(!hashMapEventWithMicroServices.containsKey(type)){
			microServicesOfEvent = new LinkedBlockingQueue<MicroService>();
		}
		else{ //type exists
			microServicesOfEvent = hashMapEventWithMicroServices.get(type); //get()->return the value of this key
			//check if MS is already subscribed
			for(MicroService ms: microServicesOfEvent){
				if(ms == m){
					found = true; //I do not want to enter m twice
				}
			}
		}
		if(!found){
			microServicesOfEvent.add(m);
			hashMapEventWithMicroServices.put(type,microServicesOfEvent);//put() -> if type exists it will only change the value of the key
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
		if(hashMapMicroserviceWithMessage.containsKey(m)){ // m is registered
			BlockingQueue<MicroService> microServicesOfBroad;
			boolean found = false;
			if( !hashMapBroadcastWithMicroServices.containsKey(type)){
				microServicesOfBroad = new LinkedBlockingQueue<MicroService>();
			}
			else{ //type exists
				microServicesOfBroad = hashMapBroadcastWithMicroServices.get(type); //get()->return the value of this key
				for(MicroService ms: microServicesOfBroad){
					if(ms == m){
						found = true; //I do not want to enter m twice
					}
				}
			}
			if(!found){
				microServicesOfBroad.add(m);
				hashMapBroadcastWithMicroServices.put(type,microServicesOfBroad); //put() -> if type exists it will only change the value of the key
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
		synchronized (hashMapMicroserviceWithMessage){
			BlockingQueue<Message> mes = new LinkedBlockingQueue <Message>();
			hashMapMicroserviceWithMessage.put(m, mes);
		}
	}

	/**
	 *
	 * @param m the microservice to unregister.
	 * if microservice.containsKey(m) == false then no need to do anything
	 */
	@Override
	public void unregister(MicroService m) {
		if(hashMapMicroserviceWithMessage.containsKey(m)){ //registered
			m.terminate();
			//remove from all the hash maps
			//microservice
			synchronized (this) {
				hashMapMicroserviceWithMessage.remove(m);
				//System.out.println(microservice_queues.get(m));
				//events
				for(BlockingQueue block:hashMapEventWithMicroServices.values()){
					if(block!=null)
						block.remove(m);
				}
				//while(events_MS.values().remove(m));
				//broadcast
				for(BlockingQueue block:hashMapBroadcastWithMicroServices.values()){
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
			if (!hashMapBroadcastWithMicroServices.isEmpty()) {
				if (hashMapBroadcastWithMicroServices.get(b.getClass()) != null) {
					BlockingQueue<MicroService> microServicesOfBroadcast = hashMapBroadcastWithMicroServices.get(b.getClass());
					for (MicroService microService : microServicesOfBroadcast) {
						synchronized (hashMapMicroserviceWithMessage.get(microService)) {
							BlockingQueue<Message> messageOfThisMicroService = hashMapMicroserviceWithMessage.get(microService);
							messageOfThisMicroService.add(b);
							hashMapMicroserviceWithMessage.put(microService, messageOfThisMicroService);
						}
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
		if (!hashMapEventWithMicroServices.isEmpty()) {
			futureOfEvent.put(e, ev_future);
			if (hashMapEventWithMicroServices.get(e.getClass()) != null) {
				synchronized (hashMapEventWithMicroServices.get(e.getClass())) {
					BlockingQueue<MicroService> microServicesOFe = hashMapEventWithMicroServices.get(e.getClass());
					MicroService firstMicroservice = microServicesOFe.poll();
					if (firstMicroservice != null) {
						BlockingQueue<Message> MessagesOfFirst = hashMapMicroserviceWithMessage.get(firstMicroservice);
						MessagesOfFirst.add(e);
						hashMapMicroserviceWithMessage.put(firstMicroservice, MessagesOfFirst);
						microServicesOFe.add(firstMicroservice);
						hashMapEventWithMicroServices.put(e.getClass(), microServicesOFe);
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
		return hashMapMicroserviceWithMessage.get(m).take();
	}

	/**
	 *
	 * check that m is subscribed to type in hashMap of event
	 * @return True or False
	 */
	public <T> boolean microserviceInEvents(Class<? extends Event<T>> type, MicroService m){
		BlockingQueue<MicroService> microServicesOFe= hashMapEventWithMicroServices.get(type);
		return (microServicesOFe.contains(m));
	}

	/**
	 *
	 * check that m is subscribed to type in hashMap of broadcast
	 * @return True or False
	 */
	public boolean microserviceInBroadcasts(Class<? extends Broadcast> type, MicroService m){
		BlockingQueue<MicroService> microServicesOFb= hashMapBroadcastWithMicroServices.get(type);
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
		BlockingQueue<Message> microserviceB = hashMapMicroserviceWithMessage.get(m);
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
		BlockingQueue<Message> microserviceE = hashMapMicroserviceWithMessage.get(m);
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
		return (hashMapMicroserviceWithMessage.containsKey(m));
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
