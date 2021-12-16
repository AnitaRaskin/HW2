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
    private int tick;

    public ConfrenceInformation(String name, int date){
        this.name = name;
        this.date = date;
        conInfo = new LinkedList<>();
        tick = 0;
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

    /**
     *
     * this service is registered to tickBroadcast so it should update tick
     * @post: size() = tick
     *        @post size = @pre size + 1
     *
     */
    public void doTick(){
        tick = tick + 1;
    }

    /**
     *
     * a getter function
     * @return tick
     */
    public int getTick(){
        return tick;
    }
}
