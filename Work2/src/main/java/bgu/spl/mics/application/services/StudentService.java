package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    //Fields
    private Student student;
    private Model currentModel ;
    Future future;


    public StudentService(String name, Student student) {
        super("studentServiceOf:"+name);
        future = null;
        this.student = student;
        currentModel = null;
    }

    /**
     *
     * this function subscribe this microservice to the proper events and broadcast that he needs to function
     * like Terminated,TickBroadcast,PublishConferenceBroadcast
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (terminate)-> terminate());
//        System.out.println(getClass()+"was initialize");
        /**
         *  for every tick send the proper event for the first cell in the Queue and then return it to the Queue
         */
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast timeB)->{
            if(currentModel==null){
                if(student.getModelQueue() != null){
                    if(student.getModelQueue().peek().getStatus().equals(Model.Status.PreTrained)) {
                        currentModel = student.getModelQueue().peek();
                        System.out.println("trying to send Model" + currentModel.getData().getType() + " StudentService 50");
                        future = sendEvent(new TrainModelEvent(currentModel));
                    }
                }
            }
            else if(future.isDone() && currentModel.getStatus().equals(Model.Status.Trained)){
                future = sendEvent(new TestModelEvent(currentModel));
                System.out.println("sent test");

            }
            else if(future.isDone() && currentModel.getStatus().equals(Model.Status.Tested) && currentModel.getResult().equals(Model.Result.Good)){
                future = sendEvent(new PublishResultsEvent(currentModel));
                student.getModelQueue().add(student.getModelQueue().poll());
                currentModel = null;
                System.out.println("publish mother FUCKER!!!!!");
            }
            else if(future.isDone() && currentModel.getStatus().equals(Model.Status.Tested) && currentModel.getResult().equals(Model.Result.Bad)){
                System.out.println("the model is not finished");
                student.getModelQueue().add(student.getModelQueue().poll());
                currentModel = null;
            }
//            if((currentModel != null) && future != null && !future.isDone()&& currentModel.getStatus().equals(Model.Status.PreTrained)){ //Pre Trained
//                TrainModelEvent trainEvent = new TrainModelEvent(currentModel);
//                future = sendEvent(trainEvent);
//                System.out.println("send to train");
//            }
//            else if((currentModel != null)){ // && future.isDone() == true -> did trained
//                if(currentModel.getStatus().equals(Model.Status.Trained)){
//                    TestModelEvent testModelEvent = new TestModelEvent(currentModel);
//                    future = sendEvent(testModelEvent);
//                    System.out.println("send to test");
//                }
//                else{ //Tested
//                    PublishResultsEvent publishReEve = new PublishResultsEvent(currentModel);
//                    future = sendEvent(publishReEve);
//                    student.getModelQueue().add(currentModel);
//                }
//            }
        });

        /**
         * all the model that are in this linkList the student "read"
         * count all the model that are belongs to this student and add them to his publications
         */
        subscribeBroadcast(PublishConferenceBroadcast.class,(PublishConferenceBroadcast publishB)->{
            LinkedList<Model> confList = publishB.getConfrenceInformation().getConInfo();
            student.addPaperRead(confList.size());
            int publications = 0;
            for(Model model : confList){
                if(student.equals(model.getStudent()))
                    publications++;
            }
            student.addPublications(publications);
        });
    }
}
