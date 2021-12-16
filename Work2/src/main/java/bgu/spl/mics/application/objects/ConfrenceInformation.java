package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {
    //Fields
    private String name;
    private int date; //its ticks
    private LinkedList<Model> goodModels;
    private int tick;

    /**
     *
     * Constructor
     * @param name name of the ConfrenceInformation will get from the input file
     * @param date - number of ticks after which it should be unregistered
     */
    public ConfrenceInformation(String name, int date){
        this.name = name;
        this.date = date;
        goodModels = new LinkedList<>();
        tick = 0;
    }

    /**
     *
     * add a "Good" model- after the student send TrainModel it will get Bad or Good and
     * the ConfrenceInformation should keep track only of the good ones, add to goodModels.
     * @param model -after TrainModel should get the result Good
     */
    public void addInfo (Model model){
        goodModels.add(model);
    }

    /**
     *
     * @return the date when it should be terminated
     */
    public int getDate() {
        return date;
    }

    /**
     *
     * @return goodModels- linked list of all the Good models after TrainModel
     */
    public LinkedList<Model> getConInfo() {
        return goodModels;
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
