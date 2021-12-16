package bgu.spl.mics;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;


class FutureTest {
    private Future<String> future;
    private Thread t1;
    private Thread t2;


    @Before
    void setUp() {
        future = new Future<>();
    }

    /**
     *
     * should check that as long as the future object isn't resolved value == null
     * it won't change anything (wait)
     * when resolved shout return the result that were resolved to this future
     */
    @Test
    void testGet() {
        assertNotNull(future.get(),"future is null"); //check if the future was not resolved
        future.resolve("check message");
        assertEquals("check message",future.get()); //check after resolve
        assertThrows(Exception.class, ()->future.get());
    }

    /**
     *
     * should check that the function really changed the value from null to result
     */
    @Test
    void testResolve() {
        future.resolve(null);
        assertEquals(null,future.get());
        assertThrows("Null is not resolved",Exception.class, ()->future.get());

        future.resolve("Check message");
        assertEquals("This is a message",future.get());
        assertThrows(Exception.class, ()->future.get());
    }

    @Test
    void testIsDone() {
        //check for empty string not resolved yet
        assertEquals(false, future.isDone());
        assertThrows("Still not resolved", Exception.class, ()->future.isDone());

        future.resolve("This is a test");
        assertEquals(true, future.isDone());
        assertThrows(Exception.class, ()->future.isDone());
    }

    @Test
    void testGetWithTime() {
        assertNull(future.get(10, TimeUnit.MICROSECONDS),"exepted null");

        //check for 0 min
        assertEquals(null, future.get(0, TimeUnit.MINUTES));
        assertThrows("time should be more then 0", Exception.class, ()->future.get(0, TimeUnit.MINUTES));

        //check when update in the middle of the run
        Thread t1 = new Thread(()-> assertEquals("This is a test", future.get(2,TimeUnit.SECONDS)));
        assertThrows("time should be more then 0", Exception.class, ()->future.get(0, TimeUnit.MINUTES));
        Thread t2 = new Thread(()->future.resolve("This is a test"));
        t1.start();
        t2.start();
    }
}