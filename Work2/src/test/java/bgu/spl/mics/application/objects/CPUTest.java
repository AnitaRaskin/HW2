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

//    @Test
//    void testProcessData() {
//        int ticksForI = (32/cpu.getCoresNum())*4;
//        int ticksForTxt = (32/cpu.getCoresNum())*2;
//        int ticksForTab = (32/cpu.getCoresNum())*1;
//        assertThrows(Exception.class,()-> cpu.processData(), "The Data collection is empty");
//        int currentSize = cpu.getData().size();
//        DataBatch dBatch = new DataBatch(Data.Type.Images);
//        cpu.getData().add(dBatch);
//        int oldTime = cpu.getTime();
//        cpu.processData();
//        assertEquals(cpu.getData().size(),currentSize);
//        assertTrue(dBatch.isProcessed());
//        assertEquals(oldTime+ticksForI,cpu.getTime());
//        dBatch = new DataBatch(Data.Type.Text);
//        cpu.getData().add(dBatch);
//        oldTime = cpu.getTime();
//        cpu.processData();
//        assertTrue(dBatch.isProcessed());
//        assertEquals(oldTime+ticksForTxt,cpu.getTime());
//        dBatch = new DataBatch(Data.Type.Tabular);
//        cpu.getData().add(dBatch);
//        oldTime = cpu.getTime();
//        cpu.processData();
//        assertTrue(dBatch.isProcessed());
//        assertEquals(oldTime+ticksForTab,cpu.getTime());
//
//    }

    @Test
    void testUpdateTime() {
        int oldTime = cpu.getTime();
        cpu.updateTime();
        assertNotEquals(oldTime, cpu.getTime());
    }
}