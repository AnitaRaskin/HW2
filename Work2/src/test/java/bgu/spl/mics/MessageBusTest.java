package bgu.spl.mics;

import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    private MessageBusImpl mb;
    private MicroService m;
    private ExampleEvent ev;
    private ExampleBroadcast broadcast;

    @BeforeEach
    void setUp() {
        mb = new MessageBusImpl();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void subscribeEvent() {
        String[] arr ={"1"};
        m = new ExampleEventHandlerService("check", arr);
        assertTrue(mb.microserviceInEvents(ExampleEvent.class,m));
        mb.subscribeEvent(ExampleEvent.class,m);
        assertFalse(mb.microserviceInEvents(ExampleEvent.class,m));



    }

    @Test
    void subscribeBroadcast() {
        String[] arr ={"1"};
        MicroService m = new ExampleBroadcastListenerService("check", arr);
        assertTrue(mb.microserviceInBroadcasts(ExampleBroadcast.class,m));
        mb.subscribeBroadcast(ExampleBroadcast.class,m);
        assertFalse(mb.microserviceInBroadcasts(ExampleBroadcast.class,m));
    }

    @Test
    void complete() {
        ev = new ExampleEvent("killbill");
        String[] arr ={"1"};
        MicroService m = new ExampleBroadcastListenerService("check", arr);
        String result = "complete";
        assertTrue(mb.isComplete(ev,result),"expected false");
        mb.complete(ev,result);
        assertFalse(mb.isComplete(ev,result),"Not expected true");

    }

    @Test
    void sendBroadcast() {
        String[] arr ={"1"};
        MicroService m = new ExampleBroadcastListenerService("check", arr);
        mb.subscribeBroadcast(ExampleBroadcast.class,m);
        broadcast = new ExampleBroadcast("check");
        assertTrue(mb.sucSendBroadcast(m));
        mb.sendBroadcast(broadcast);
        assertFalse(mb.sucSendBroadcast(m));

    }

    @Test
    void sendEvent() {
        String[] arr ={"1"};
        MicroService m = new ExampleBroadcastListenerService("check", arr);
        mb.subscribeEvent(ExampleEvent.class,m);
        ev = new ExampleEvent("check");
        assertTrue(mb.sucSendEvent(m));
        mb.sendEvent(ev);
        assertFalse(mb.sucSendEvent(m));
    }

    @Test
    void register() {
        String[] arr ={"1"};
        MicroService m = new ExampleBroadcastListenerService("check", arr);
        assertTrue(mb.isMicroServiceRegistered(m));
        mb.register(m);
        assertFalse(mb.isMicroServiceRegistered(m));
    }

    @Test
    void unregister() {
        String[] arr ={"1"};
        MicroService m = new ExampleBroadcastListenerService("check", arr);
        mb.register(m);
        assertFalse(mb.isMicroServiceRegistered(m));
        mb.subscribeBroadcast(ExampleBroadcast.class,m);
        mb.subscribeEvent(ExampleEvent.class,m);
        assertFalse(mb.microserviceInEvents(ExampleEvent.class,m));
        assertFalse(mb.microserviceInBroadcasts(ExampleBroadcast.class,m));
        mb.unregister(m);
        assertTrue(mb.isMicroServiceRegistered(m));
        assertTrue(mb.microserviceInEvents(ExampleEvent.class,m));
        assertTrue(mb.microserviceInBroadcasts(ExampleBroadcast.class,m));
    }

    @Test
    void awaitMessage() {
        String[] arr ={"1"};
        MicroService m = new ExampleBroadcastListenerService("check", arr);
        mb.register(m);
        ev = new ExampleEvent("check");
        mb.sendEvent(ev);
        assertFalse(mb.hasAwaitMassage(m));
        try {
            assertEquals(mb.awaitMessage(m),ev);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(mb.hasAwaitMassage(m));
    }
}