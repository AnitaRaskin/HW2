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
    //private MessageBus messageBus = MessageBusImpl.getInstance();
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

        /**
         *  for every tick send the proper event for the first cell in the Queue and then return it to the Queue
         */
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast timeB)->{
            if(currentModel==null){
                if(student.getModelQueue() != null){
                    currentModel = student.getModelQueue().poll();
                }
            }
            if((currentModel != null) && future != null && !future.isDone()){ //Pre Trained
                TrainModelEvent trainEvent = new TrainModelEvent(currentModel);
                future = sendEvent(trainEvent);
            }
            else if((currentModel != null)){ // && future.isDone() == true -> did trained
                if(currentModel.getStatus().equals(Model.Status.Trained)){
                    TestModelEvent testModelEvent = new TestModelEvent(currentModel);
                    future = sendEvent(testModelEvent);
                }
                else{ //Tested
                    PublishResultsEvent publishReEve = new PublishResultsEvent(currentModel);
                    future = sendEvent(publishReEve);
                    student.getModelQueue().add(currentModel);
                }
            }
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
                if(student == model.getStudent())
                    publications++;
            }
        });
    }
}
