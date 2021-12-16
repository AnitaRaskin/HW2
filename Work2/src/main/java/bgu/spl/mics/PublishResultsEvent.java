package bgu.spl.mics;
import bgu.spl.mics.application.objects.Model;

/**
 * This class is activated by the student.
 * After student send a TrainModel event it waits for a result of Good or Bad for that model
 * when a result is decided it should send only the Good models to the ConfrenceInformation
 */
public class PublishResultsEvent implements Event{
    //Fields
    private Model model;

    /**
     *
     * Constructor
     * @param model -should have the result of Good
     */
    public PublishResultsEvent (Model model){
        this.model = model;
    }

    /**
     *
     * @return the Model
     */
    public Model getModel() {
        return model;
    }
}
