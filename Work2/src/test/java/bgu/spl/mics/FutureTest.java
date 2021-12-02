package bgu.spl.mics;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import java.util.concurrent.TimeUnit;


class FutureTest {
    private Future<String> future;
    private Thread t1;
    private Thread t2;


    @Before
    void setUp() {
        future = new Future<>();
    }

    @Test
    void testget() {
        assertNotNull(future.get(),"future is null");
        future.resolve("This is a message");
        assertEquals("This is a message",future.get());
        assertThrows(Exception.class, ()->future.get());
    }

    @org.junit.jupiter.api.Test
    void resolve() {
        future.resolve(null);
        assertEquals(null,future.get());
        assertThrows("Null is not resolved",Exception.class, ()->future.get());

        future.resolve("This is a message");
        assertEquals("This is a message",future.get());
        assertThrows(Exception.class, ()->future.get());
    }

    @org.junit.jupiter.api.Test
    void isDone() {
        //check for empty string not resolved yet
        assertEquals(false, future.isDone());
        assertThrows("Still not resolved", Exception.class, ()->future.isDone());

        future.resolve("This is a test");
        assertEquals(true, future.isDone());
        assertThrows(Exception.class, ()->future.isDone());
    }

    @org.junit.jupiter.api.Test
    void testGet1() {
        assertNull(future.get(10, TimeUnit.MICROSECONDS),"exepted null");

        //check for 0 min
        assertEquals(null, future.get(0, TimeUnit.MINUTES));
        assertThrows("time should be more then 0", Exception.class, ()->future.get(0, TimeUnit.MINUTES));

        //check when update in the middle of the run
        Thread t1 = new Thread(()->assertEquals("This is a test", future.get(10,TimeUnit.SECONDS)));
        Thread t2 = new Thread(()->future.resolve("This is a test"));
        t1.start();
        t2.start();

    }
}