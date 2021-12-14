package bgu.spl.mics.application.objects;

import java.util.Locale;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;
    public Data(String type, int size){
        this.type = Type.valueOf(type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
        this.size = size;
        processed = 0;
    }
    public void addTrainedData(){
        processed+=1000;
    }
    public boolean dataTrained(){
        return (processed==size);
    }
    public Type getType() {
        return type;
    }
    public int getSize(){
        return size;
    }
}
