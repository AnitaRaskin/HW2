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
    private int DataBatchSize;

    public GPU(String type, Model model){
        DataBatchSize = 0;
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
//        System.out.println("number of DataBatches "+Disk.size()+ " GPU-78");
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
    public void sendDataToPro(){
        //System.out.println(type);
        //System.out.println(model.getData().getSize());
        while(Disk.size() > 0 && memoryLimitation > 0){//able to train dataBatch
            DataBatch db = Disk.remove();
            memoryLimitation--;
            cluster.takeDataToProc(db);
        }
    }

    /**
     *
     * this function send processed databatch to the gpuService to be trained
     * @pre: VRAM != null
     * @post: size() = VRAM
     *        @post size() = @pre size() - 1
     */
    private void trainBatch(){
        if(VRAM.size() > 0){
//            System.out.println("VRAm>0 GPU114");
            if(ticks == neededTicks()){
                //System.out.println("have enough ticks");
                model.getData().addTrainedData();
                VRAM.poll();
                runTime += ticks;
                ticks=0;
                memoryLimitation++;
                DataBatchSize ++;
                sendDataToPro();
//                System.out.println("I train Data, current Time used: "+runTime+ "GPU121" );
            }
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
    public void testModel(){
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
//        System.out.println("Data added to VRAM gpu174");
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
//        System.out.println("did TIck GPU190");
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
    public int getRunTime(){
        return runTime;
    }
    public int getDataBatchSize(){
        return DataBatchSize;
    }

}
