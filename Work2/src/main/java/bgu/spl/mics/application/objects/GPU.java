package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;
import java.util.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    //Fields
    private Type type;
    private Cluster cluster;
    private Model model = null;
    private Queue<DataBatch> Disk;//store unprocessed data, doesn't have space limitation
    private Queue<DataBatch> VRAM;//store processed data, have space limitation
    private int memoryLimitation;
    private int ticks;
    private int runTime=0;

    public GPU(String type, Model model){
        this.type = Type.valueOf(type);
        cluster = Cluster.getInstance();
        this.model = model;
        Disk = new LinkedList<DataBatch>();
        VRAM = new LinkedList<DataBatch>();
        if(this.type == Type.RTX3090){
            memoryLimitation = 32;
        }
        else if(this.type == Type.RTX2080){
            memoryLimitation = 16;
        }
        else{
            memoryLimitation = 8;
        }
        ticks=0;
    }

    /**
     * this function update the model that the gpu id working on
     * @pre: None
     * @post: model = new_model
     */
    public  void updateModel(Model model){
        this.model = model;
    }

    /**
     *
     * return the model that the gpu id working on
     * @pre: None
     * @post: None
     */
    public Model getModel(){
        return model;
    }

    /**
     * this function get the data that in the model and then split it to samples with the size 1000 to dataBatches
     * and store them in the disk
     * for example data with the size of 10,000 will split to 10 dataBatches
     * @pre: data.size() > 0
     * @post: None
     */
    public void splitDataToBatches(){
        Data data = model.getData();
        int size = data.getSize();
        for(int i=0; i < size ; i += 1000){
            DataBatch db = new DataBatch(data, i);
            Disk.add(db);
        }
    }

    /**
     * return Disk.Queue
     * @pre: None
     * @post: None
     */
    public Queue<DataBatch> getDisk(){
        return Disk;
    }

    /**
     * this function send to the cluster dataBatch to be processed by the CPUS
     * will send only if it have the memory in the
     * @pre: Disk.size() > 0
     * @post: size() = Disk
     *      *        @post size() = @pre size() - 1
     */
    public boolean sendDataToPro(){
        if(memoryLimitation > 0){//able to train dataBatch
            DataBatch db = Disk.remove();
            memoryLimitation--;
            cluster.takeDataToProc(db);
            return true;
        }
        return false;
    }

    /**
     *
     * this function send processed databatch to the gpuService to be trained
     * @pre: VRAM != null
     * @post: size() = VRAM
     *        @post size() = @pre size() - 1
     */
    private void trainBatch(){
        if(VRAM != null){
            if(ticks == neededTicks()){
                model.getData().addProcessed();
                VRAM.poll();
            }
            memoryLimitation++;
            sendDataToPro();
        }
        else
            ticks = 0;
    }

    /**
     * this function calculate the ticks that us needed to train dataBatch
     * @return the amount of ticks that are needed
     */
    private int neededTicks(){
        if(type == Type.RTX3090)
            return 1;
        else if(type == Type.RTX2080)
            return 2;
        else
            return 3;
    }
    /**
     * this function send processed data batch to the gpuService to be trained
     * @pre: VRAM != null
     * @post: size() = VRAM
     *        @post size() = @pre size() - 1
     */
    public void testBatch(){
        Student student = model.getStudent();
        if(student.getStatus() == Student.Degree.MSc){
            int random = (int)(Math.random()*10);
            if(random <= 0.6)
                model.updateResult(true);
            else
                model.updateResult(false);
        }
        else{
            int random = (int)(Math.random()*10);
            if(random <= 0.8)
                model.updateResult(true);
            else
                model.updateResult(false);
        }
    }

    /**
     *
     * this function add processed dataBatch to the VRAM
     * @pre: memoryLimitation < memoryLimitation.max-processed.memory
     * @inv: memoryLimitation < memoryLimitation.max
     * @post: @post memoryLimitation = @pre memoryLimitation + processed.memory
     */
    public void addProcessedData(DataBatch dataBatch){
        VRAM.add(dataBatch);
    }

    /**
     *
     * @pre: None
     * @post: None
     */
    public Queue<DataBatch> getVRAM(){
        return VRAM;
    }

    /**
     *
     * @pre: ticks >=0
     * @post: @post tick = @pre tick +1
     */
    public void doTick(){
        ticks++;
        trainBatch();
    }

    /**
     *
     * @pre: None
     * @post: None
     */
    public int getTicks(){
        return ticks;
    }

}
