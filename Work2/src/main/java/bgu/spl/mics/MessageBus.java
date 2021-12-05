package bgu.spl.mics;

/**
 * The message-bus is a shared object used for communication between
 * micro-services.
 * It should be implemented as a thread-safe singleton.
 * The message-bus implementation must be thread-safe as
 * it is shared between all the micro-services in the system.
 * You must not alter any of the given methods of this interface. 
 * You cannot add methods to this interface.
 */
public interface MessageBus {

    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     *
     * @pre: m!=null
     * @post: None
     */
    <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m);

    /**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * @param type 	The type to subscribe to.
     * @param m    	The subscribing micro-service.
     * @pre: m!=null
     * @post: None
     */
    void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m);

    /**
     * Notifies the MessageBus that the event {@code e} is completed and its
     * result was {@code result}.
     * When this method is called, the message-bus will resolve the {@link Future}
     * object associated with {@link Event} {@code e}.
     * Explanation: send notify to the future of this message + resolve it with the result
     * <p>
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     *
     * @pre: result != null && e!=null
     * @inv: result.instanceOf() == T
     * @post:
     */
    <T> void complete(Event<T> e, T result);

    /**
     * Adds the {@link Broadcast} {@code b} to the message queues of all the micro-services subscribed to {@code b.getClass()}.
     * Explanation: all micro-services subscribed to b.type Broadcast we need to find them and give them the b as a message
     *
     * <p>
     * @param b 	The message to added to the queues.
     *
     * @pre: b != null
     * @post: size() = message_queues
     *          @post size() = @ pre size() + 1
     */
    void sendBroadcast(Broadcast b);

    /**
     * Adds the {@link Event} {@code e} to the message queue of one of the micro-services subscribed to
     * {@code e.getClass()} in a round-robin fashion. This method should be non-blocking.
     * Explanation: all micro-services subscribed to e.getClass and the messages are added in round-robin
     * return Future object-> to this he can return the result if there is no suitable MS return null
     * <p>
     * @param <T>    	The type of the result expected by the event and its corresponding future object.
     * @param e     	The event to add to the queue.
     *
     * @pre: e != null
     * @post: size() = message_queues
     *        @post size() = @ pre size() + 1
     *        Future<T> = null;
     * @return {@link Future<T>} object to be resolved once the processing is complete,
     * 	       null in case no micro-service has subscribed to {@code e.getClass()}.
     */
    <T> Future<T> sendEvent(Event<T> e);

    /**
     * Allocates a message-queue for the {@link MicroService} {@code m}.
     * <p>
     * @param m the micro-service to create a queue for.
     *
     * @pre: m != null && m.Queue = null
     * @post: m.Queue.size() = 0
     */
    void register(MicroService m);

    /**
     * Removes the message queue allocated to {@code m} via the call to {@link #register(bgu.spl.mics.MicroService)}
     * and cleans all references related to {@code m} in this message-bus. If {@code m} was not
     * registered, nothing should happen.
     * <p>
     * @param m the micro-service to unregister.
     *
     * @pre: m != null
     * @post: m.Queue = null
     */
    void unregister(MicroService m);

    /**
     * Using this method, a <b>registered</b> micro-service can take message from its allocated queue.
     * This method is blocking meaning that if no messages are available in the micro-service queue it
     * should wait until a message becomes available.
     * The method should throw the {@link IllegalStateException} in the case where {@code m} was never registered.
     * <p>
     * @param m The micro-service requesting to take a message from its message queue.
     *
     * @pre: m != null
     * @post: if(m registered){ size() = m.Queue
     *                          @post size() = @pre size() -1}
     *        else{ throws InterruptedException}
     * @return The next message in the {@code m}'s queue (blocking).
     * @throws InterruptedException if interrupted while waiting for a message to became available.
     *
     */
    Message awaitMessage(MicroService m) throws InterruptedException;

    /**
     * @pre: e != null && result != null
     * @post: none
     * @param e the event that we check if we compelte
     * @param result the result we have
     * @param <T> the type of the result
     * @return true if succeed else false
     */
    public <T> boolean isComplete(Event<T> e, T result);

    /**
     * @pre: m!=null
     * @post: none
     * @param m the microService that will get the massage
     * @return true if succeed else false
     */
    public boolean sucSendBroadcast(MicroService m);

    /**
     * @pre: m!=null
     * @post: none
     * @param m the microService that will get the massage
     * @return true if succeed else false
     */
    public boolean sucSendEvent(MicroService m);

    /**
     * @pre: m!=null
     * @post: none
     * @param m the microService that will registered
     * @return true if succeed else false
     */
    public boolean isMicroServiceRegistered(MicroService m);

    /**
     * @pre: m!=null
     * @post: none
     * @param m the microService we want tp get massage from him
     * @return true if succeed else false
     */
    public boolean hasAwaitMassage(MicroService m);
    
}
