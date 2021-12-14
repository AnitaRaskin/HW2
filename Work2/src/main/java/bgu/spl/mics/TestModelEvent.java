package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event<Model>{

    Model model;
    Future<TestModelEvent> future = new Future<>();
    public TestModelEvent (Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Future<TestModelEvent> getFuture() {
        return future;
    }

    public void setFuture(Future<TestModelEvent> future) {
        this.future = future;
    }
}
