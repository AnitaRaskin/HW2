package bgu.spl.mics;

import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String result = "complete";

        mb.complete(ev,result);
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
    }

    @Test
    void awaitMessage() {
    }
}