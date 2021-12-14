package bgu.spl.mics;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model>{

    Model model;
    Future<TrainModelEvent> future = new Future<>();
    public TrainModelEvent (Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Future<TrainModelEvent> getFuture() {
        return future;
    }

    public void setFuture(Future<TrainModelEvent> future) {
        this.future = future;
    }
}
