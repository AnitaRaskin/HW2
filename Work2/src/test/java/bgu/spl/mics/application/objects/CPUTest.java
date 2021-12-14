package bgu.spl.mics.application.objects;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

class
CPUTest {
    private CPU cpu;




    @Before
    void setUp() {
        cpu = new CPU(4);

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