package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private Queue<Model> modelQueue;
    private int publications;
    private int papersRead;
    public Student(String name, String department, String status){
        this.name = name;
        this.department = department;
        this.status = Degree.valueOf(status);
        modelQueue = new LinkedList<Model>();
        publications = 0;
        papersRead = 0;
    }

    public void addModel(Model model){
        modelQueue.add(model);
    }

    public String getName(){
        return name;
    }

    public Queue<Model> getModelQueue() {
        return modelQueue;
    }

    public void addPublications(){
        publications++;
    }

    public void addPaperRead(){
        papersRead++;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public Degree getStatus() {
        return status;
    }
}
