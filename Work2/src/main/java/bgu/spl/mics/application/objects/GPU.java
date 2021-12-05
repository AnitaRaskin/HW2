package bgu.spl.mics.application.objects;

import java.util.Queue;

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

    private Type type;
    private Cluster cluster;
    private Model model ;
    private Queue<DataBatch> Disk;//store unprocessed data, doesn't have space limitation
    private Queue<DataBatch> VRAM;//store processed data, have space limitation
    private int memoryLimitation;
    private int ticks;

    public GPU(Type type){

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
     * return the model that the gpu id working on
     * @pre: None
     * @post: None
     */
    public Model getModel(){
        return model;
    }

    /**
     * this function get the data that in the model and then split it to 1000 samples of dataBatches
     * help function
     * @pre: Disk.size() > 0
     * @post: None
     */
    public void splitDataToBatches(){

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
     * help function- used from Cluster
     * @pre: Disk.size() > 0
     * @post: size() = Disk
     *      *        @post size() = @pre size() - 1
     */
    public void sendDataToProc(){

    }
    /**
     * this function send processed databatch to the gpuService to be trained
     * @pre: VRAM != null
     * @post: size() = VRAM
     *        @post size() = @pre size() - 1
     */
    public void trainBatch(){

    }

    /**
     * this function add processed dataBatch to the VRAM
     * @pre: memoryLimitation < memoryLimitation.max-processed.memory
     * @inv: memoryLimitation < memoryLimitation.max
     * @post: @post memoryLimitation = @pre memoryLimitation + processed.memory
     */
    public void addProcessedData(DataBatch dataBatch){

    }
    /**
     * @pre: None
     * @post: None
     */
    public Queue<DataBatch> getVRAM(){
        return VRAM;
    }
    /**
     * @pre: ticks >=0
     * @post: @post tick = @pre tick +1
     */
    public void doTick(){
        ticks++;
    }
    /**
     * @pre: None
     * @post: None
     */
    public int getTicks(){
        return ticks;
    }



}
