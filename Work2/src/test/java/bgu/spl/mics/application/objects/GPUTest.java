package bgu.spl.mics.application.objects;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;

class GPUTest {

    GPU gpu, gpu_mid, gpu_slow;

    @Before
    void setUp() {
        Student student = new Student("Checker","Uni", "MSc");
        Data data = new Data("Images",1000);
        Model model = new Model("test",data,student);
        gpu = new GPU("RTX3090",model);
        gpu_mid = new GPU("RTX2080",model);
        gpu_slow = new GPU("GTX1080",model);
    }

    @Test
    void updateModel() {
        Student student = new Student("Checker","Uni", "MSc");
        Data data = new Data("Images",1000);
        Model model = new Model("test",data,student);
        assertEquals(null,gpu.getModel(),"Excepted the model to be null");
        gpu.updateModel(model);
        assertNotEquals(model,gpu.getModel());
    }

    @Test
    void splitDataToBatches() {
        Student student = new Student("Checker","Uni", "MSc");
        Data data = new Data("Images",1000);
        Model model = new Model("test",data,student);
        assertEquals(0,gpu.getDisk().size());
        gpu.splitDataToBatches();
        assertEquals(1,gpu.getDisk().size());
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
        Student student = new Student("Checker","Uni", "MSc");
        Data data = new Data("Images",1000);
        Model model = new Model("test",data,student);
        gpu.splitDataToBatches();
        assertEquals(1,gpu.getDisk().size());
        gpu.sendDataToPro();
        assertEquals(0,gpu.getDisk().size());
    }

    @Test
    void doTick() {
        int currentTick = gpu.getTicks();
        gpu.doTick();
        assertEquals(currentTick+1, gpu.getTicks());
    }

}