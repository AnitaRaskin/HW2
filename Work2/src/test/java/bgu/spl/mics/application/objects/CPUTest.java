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
        int currentSize = cpu.getData().size();
        cpu.getData().add(new DataBatch());
        assertEquals(currentSize,cpu.getData().size());
    }

    @Test
    void testUpdateTime() {
        int oldTime = cpu.getTime();
        cpu.updateTime();
        assertNotEquals(oldTime, cpu.getTime());
    }
}