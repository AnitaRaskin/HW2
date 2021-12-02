package bgu.spl.mics.application.objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Timer;

import static org.junit.jupiter.api.Assertions.*;

class
CPUTest {
    private CPU cpu;




    @BeforeEach
    void setUp() {
        cpu = new CPU(4);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testReceiveData() {
        long currentSize = cpu.getData().size();
        cpu.getData().add(new DataBatch());
        assertEquals(currentSize,cpu.getData().size());
    }

    @Test
    void testProcessData() {
        assertThrows(Exception.class,()-> cpu.processData(), "The Data collection is empty");
        long currentSize = cpu.getData().size();
        DataBatch dBatch = new DataBatch();
        cpu.getData().add(dBatch);
        Timer oldTime = cpu.getTime();
        cpu.processData();
        assertEquals(cpu.getData().size(),currentSize);
        assertTrue(dBatch.isProcessed());
        assertNotEquals(oldTime,cpu.getTime());
    }

    @Test
    void testSentData() {//maybe the claster will do that
    }

    @Test
    void testUpdateTime() {
        Timer oldTime = cpu.getTime();
        cpu.updateTime();
        assertNotEquals(oldTime, cpu.getTime());
    }
}