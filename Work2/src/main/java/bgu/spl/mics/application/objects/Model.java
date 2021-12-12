package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    enum Status{PreTrained, Training, Trained, Tested};
    enum Result{None, Good, Bad};

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Data getData() {
        return data;
    }
}
