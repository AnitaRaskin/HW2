package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status{PreTrained, Training, Trained, Tested};
    public enum Result{None, Good, Bad};

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String name, Data data, Student student){
        this.name = name;
        this.data = data;
        this.student = student;
        status = Status.PreTrained;
        result = Result.None;
    }
    public void updateStatus(){
        if(status==Status.PreTrained)
            status = Status.Training;
        else if(status==Status.Training)
            status = Status.Trained;
        else
            status = Status.Tested;
    }

    public Result getResult() {
        return result;
    }

    public Status getStatus() {
        return status;
    }

    public void updateResult(boolean resultB){
        if(resultB)
            result = Result.Good;
        else
            result = Result.Bad;
    }
    public Data getData() {
        return data;
    }
    public Student getStudent(){
        return student;
    }
    public String getName(){
        return name;
    }
    public Boolean toPublish(){
        return Result.Good == result;
    }
}
