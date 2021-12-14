package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event{
    Model model;
    Future<PublishResultsEvent> future = new Future<>();
    public PublishResultsEvent (Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Future<PublishResultsEvent> getFuture() {
        return future;
    }

    public void setFuture(Future<PublishResultsEvent> future) {
        this.future = future;
    }
}
