package bgu.spl.mics;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model>{

    Model model;
    public TrainModelEvent (Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
