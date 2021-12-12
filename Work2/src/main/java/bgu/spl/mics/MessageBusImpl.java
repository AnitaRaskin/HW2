package bgu.spl.mics;
import java.util.Hashtable;
import java.util.Vector;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//Fields
	private Hashtable events = new Hashtable(); // key = event type ; value = vector of MS
	private Hashtable broadcasts = new Hashtable(); // key = broadcast type ; value = vector of MS
	private Hashtable microservice = new Hashtable(); //key = m ; value = vector of messages


	/**
	 *
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 * add a microservice to the value(=vector of MS) of a type of Message(=key) in the hush
	 * @pre: microservice.containsKey(m) == true ->m is registered
	 *
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(microservice.containsKey(m)){ // m is registered
			Vector<MicroService> vec;
			Boolean found = false;
			if(!events.containsKey(type)){
				vec = new Vector<MicroService>();
			}
			else{ //type exists
				vec = (Vector<MicroService>) events.get(type); //get()->return the value of this key
				for(MicroService ms: vec){
					if(ms == m){
						found = true; //I do not want to enter m twice
					}
				}
			}
			if(!found){
				vec.add(m);
				events.put(type,vec);//put() -> if type exists it will only change the value of the key
				                     //         else create a new one and add the key and the value
			}
		}
	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 * add a microservice to the value(=vector of MS) of a type of Broadcast(=key) in the hush
	 * @pre: microservice.containsKey(m) == true ->m is registered
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(microservice.containsKey(m)){ // m is registered
			Vector<MicroService> vec;
			Boolean found = false;
			if(!broadcasts.containsKey(type)){
				vec = new Vector<MicroService>();
			}
			else{ //type exists
				vec = (Vector<MicroService>) broadcasts.get(type); //get()->return the value of this key
				for(MicroService ms: vec){
					if(ms == m){
						found = true; //I do not want to enter m twice
					}
				}
			}
			if(!found){
				vec.add(m);
				broadcasts.put(type,vec); //put() -> if type exists it will only change the value of the key
				                          //         else create a new one and add the key and the value
			}
		}

	}

	/**
	 *
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * call the function of Event that will call the function of Future resolve
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		e.setResolve(result);
	}

	/**
	 *
	 * @param m the microservice to create a queue for.
	 * @pre:  None
	 * @post: microservice.containsKey(m) == true
	 */
	@Override
	public void register(MicroService m) {
		Vector<Message> vec = new Vector<Message>();
		microservice.put(m, vec); //key = m ; value = vec
	}

	/**
	 *
	 * @param m the microservice to unregister.
	 * if microservice.containsKey(m) == false then no need to do anything
	 */
	@Override
	public void unregister(MicroService m) {
		if(microservice.containsKey(m)){ //registered
			//???????????????????????????????????????
			//remove from all the hash maps
			//microservice
			microservice.remove(m);
			//events
			events.values().remove(m);
			//broadcast
			broadcasts.values().remove(m);
		}

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
