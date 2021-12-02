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
    private Timer time;

    public CPU(int cores){ //constructor
        this.cores = cores;
        data = new LinkedList<DataBatch>(); //we will take the data from the cluster
        cluster = Cluster.getInstance();
        time= null;//??????
    }

    //before- receive data
    public void receiveData(){
        //data.addLast(cluster.getData());
    }

    //middle- process data
    public void processData(Data d){
        if(d.getType() == Data.Type.Images){//Images
            processingTime=(32/cores)*4;
        }
        else if(d.getType() == Data.Type.Text){//Text
            processingTime=(32/cores)*2;
        }
        else{//Tabular
            processingTime=(32/cores)*1;
        }
        sentData();
    }


    //after- return data
    public void sentData (){
        //delete the unprocessed data
        //put in that place the processed data
    }

    public void updateTime(){

    }

}
