package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Data data;
    private int start_index;
    private Boolean processed;
    Data.Type type;
    public DataBatch(){
        processed = false;
    }
    //this constructor is not correct
    public DataBatch(Data.Type type){
        this.type = type;
        processed = false;
    }
    public Data.Type getType(){
        return data.getType();
    }
    public DataBatch(Data data, int start_index){
        processed = false;
        this.data = data;
        this.start_index = start_index;
    }
    public void doneProcessed(){
        processed = true;
    }
    public boolean isProcessed(){
        return processed;
    }
}
