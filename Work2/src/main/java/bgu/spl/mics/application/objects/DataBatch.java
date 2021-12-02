package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Boolean processed;
    public DataBatch(){
        processed = false;
    }
    public void doneProcessed(){
        processed = true;
    }
    public boolean isProcessed(){
        return processed;
    }
}
