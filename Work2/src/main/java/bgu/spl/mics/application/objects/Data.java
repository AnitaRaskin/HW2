package bgu.spl.mics.application.objects;

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
    private Data(Type type, int size){
        this.type = type;
        this.size = size;
        processed = 0;
    }
    public void addProcessed(){
        processed++;
    }
    public boolean dataProcessed(){
        return (processed==size);
    }
    public Type getType() {
        return type;
    }
}
