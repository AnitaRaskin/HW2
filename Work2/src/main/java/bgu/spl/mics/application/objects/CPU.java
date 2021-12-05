package bgu.spl.mics.application.objects;

import java.awt.*;
import java.sql.Time;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    //Fields
    private int cores;
    private LinkedList<DataBatch> data; //Collection
    private Cluster cluster;
    private int tick;

    public CPU(int cores){ //constructor
        this.cores = cores;
        data = new LinkedList<DataBatch>(); //we will take the data from the cluster
        cluster = Cluster.getInstance();
        tick= 0;//??????
    }

    /**
     * before- Cluster send data to the CPU
     * @pre: None
     * @post: None
     */
    public void receiveData(){
        //data.addLast(cluster.getData());
    }

    /**
     * middle- need to process the data use CPUS
     * @pre: data.size() > 0
     * @post: size() = data
     *        @post size() = @pre size() -1
     */
    public void processData(){
//        long processingTime;
//        if(d.getType() == Data.Type.Images){//Images
//            processingTime=(32/cores)*4;
//        }
//        else if(d.getType() == Data.Type.Text){//Text
//            processingTime=(32/cores)*2;
//        }
//        else{//Tabular
//            processingTime=(32/cores)*1;
//        }
//        sentData();
    }

    /**
     * update the tick
     * @pre: tick >=0
     * @post: @post tick = @pre tick +1
     */
    public void updateTime(){

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
    public LinkedList<DataBatch> getData(){
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
