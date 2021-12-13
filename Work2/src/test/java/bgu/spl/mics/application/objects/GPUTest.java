package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {

    GPU gpu, gpu_mid, gpu_slow;

    @BeforeEach
    void setUp() {
        Student student = new Student("Checker","Uni", Student.Degree.MSc);
        Data data = new Data(Data.Type.Images,1000);
        Model model = new Model("test",data,student);
        gpu = new GPU(GPU.Type.RTX3090,model);
        gpu_mid = new GPU(GPU.Type.RTX2080,model);
        gpu_slow = new GPU(GPU.Type.GTX1080,model);
    }

    @Test
    void updateModel() {
        Student student = new Student("Checker","Uni", Student.Degree.MSc);
        Data data = new Data(Data.Type.Images,1000);
        Model model = new Model("test",data,student);
        assertNotNull(gpu.getModel(), "Excepted the model to be null");
        gpu.updateModel(model);
        assertNotEquals(model,gpu.getModel());
    }

    @Test
    void splitDataToBatches() {
        Student student = new Student("Checker","Uni", Student.Degree.MSc);
        Data data = new Data(Data.Type.Images,1000);
        Model model = new Model("test",data,student);
        assertEquals(0,gpu.getDisk().size());
        gpu.splitDataToBatches();
        assertEquals(1000,gpu.getDisk().size());
    }

    @Test
    void addProcessedData() {
        DataBatch dataBatch = new DataBatch();
        assertEquals(0,gpu.getVRAM().size());
        gpu.addProcessedData(dataBatch);
        assertEquals(1,gpu.getVRAM().size());
    }

    @Test
    void sendDataToProc() {
        Student student = new Student("Checker","Uni", Student.Degree.MSc);
        Data data = new Data(Data.Type.Images,1000);
        Model model = new Model("test",data,student);
        gpu.splitDataToBatches();
        assertEquals(1000,gpu.getDisk().size());
        gpu.sendDataToPro();
        assertEquals(999,gpu.getDisk().size());
    }

    @Test
    void doTick() {
        int currentTick = gpu.getTicks();
        gpu.doTick();
        assertEquals(currentTick+1, gpu.getTicks());
    }


    @Test
    void trainBatch() {
        DataBatch dataBatch = new DataBatch();
        gpu.addProcessedData(dataBatch);
        assertEquals(1,gpu.getVRAM().size());
        gpu_mid.addProcessedData(dataBatch);
        assertEquals(1,gpu_mid.getVRAM().size());
        gpu_mid.addProcessedData(dataBatch);
        assertEquals(1,gpu_mid.getVRAM().size());
        int currentTick = gpu.getTicks();
        gpu.trainBatch();
        assertEquals(currentTick+1,gpu.getTicks());
        assertEquals(0,gpu.getVRAM().size());
        int currentTickM = gpu_mid.getTicks();
        gpu_mid.trainBatch();
        assertEquals(currentTickM+2,gpu_mid.getTicks());
        assertEquals(0,gpu_mid.getVRAM().size());
        int currentTickS = gpu_slow.getTicks();
        gpu_slow.trainBatch();
        assertEquals(currentTickS+3,gpu_slow.getTicks());
        assertEquals(0,gpu_slow.getVRAM().size());
    }
}