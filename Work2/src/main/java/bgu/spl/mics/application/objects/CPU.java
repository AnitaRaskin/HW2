package bgu.spl.mics.application.objects;

import java.awt.*;
import java.sql.Time;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    //Fields
    private int cores;
    private Queue<DataBatch> data; //Collection
    private Cluster cluster;
    private int tick;

    public CPU(int cores){ //constructor
        this.cores = cores;
        data = new LinkedList<DataBatch>(); //we will take the data from the cluster
        cluster = Cluster.getInstance();
        tick= 0;
    }

    /**
     * before- Cluster send data to the CPU
     * @pre: None
     * @post: None
     */
    public void receiveData(DataBatch dataBatch){
        data.add(dataBatch);
    }

    /**
     * middle- need to process the data use CPUS
     * @pre: data.size() > 0
     * @post: size() = data
     *        @post size() = @pre size() -1
     */
    public void processData(){
        if(data!=null) {
            DataBatch corrent = data.peek();
            if (tick == processingTime(corrent.getType())) {
                cluster.sentData(data.remove());
                tick = 0;
            }
        }
    }

    /**
     * this function calculate the time need to process this dataBatch
     * @param type
     * @return
     */
    private int processingTime(Data.Type type){
        if(type == Data.Type.Images){//Images
            return (32/cores)*4;
        }
        else if(type == Data.Type.Text){//Text
            return (32/cores)*2;
        }
        else{//Tabular
            return (32/cores)*1;
        }
    }
    /**
     * update the tick
     * @pre: tick >=0
     * @post: @post tick = @pre tick +1
     */
    public void updateTime(){
        tick++;
        processData();
    }

    /**
     * return tick
     * @pre: None
     * @post: None
     */
    public int getTime(){
        return tick;
    }

    /**
     * return the LinkedList data
     * @pre: None
     * @post: None
     */
    public Queue<DataBatch> getData(){
        return data;
    }

    /**
     * return number of cores
     * @pre: None
     * @post: None
     */
    public int getCoresNum(){
        return cores;
    }

}
