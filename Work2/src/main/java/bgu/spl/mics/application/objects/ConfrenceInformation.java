package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> conInfo;

    public ConfrenceInformation(String name, int date){
        this.name = name;
        this.date = date;
        conInfo = new LinkedList<>();
    }

    public void addInfo (Model model){
        conInfo.add(model);
    }

    public int getDate() {
        return date;
    }

    public LinkedList<Model> getConInfo() {
        return conInfo;
    }
}
