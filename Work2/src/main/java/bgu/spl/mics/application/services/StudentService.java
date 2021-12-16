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
    private Student student;
    private MessageBus messageBus = MessageBusImpl.getInstance();
    public StudentService(String name, Student student) {
        super("studentServiceOf:"+name);
        this.student = student;
    }

    @Override
    /**
     * this function subscribe this microservice to the proper
     * events and broadcast that he needs to function
     * like Terminated,TickBroadcast,PublishConferenceBroadcast
     */
    protected void initialize() {
        subscribeBroadcast(Terminated.class, (Terminated terminated)->{
            terminate();
        });
        /**
         *  for every tick send the proper event for the first cell in the Queue and then return it to the Queue
         */
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast timeB)->{
            Model studentModel = student.getModelQueue().poll();
            if(studentModel.getStatus() == Model.Status.PreTrained){
                TrainModelEvent trainEvent = new TrainModelEvent(studentModel);
                sendEvent(trainEvent);
            }
            else if(studentModel.getStatus() == Model.Status.Trained){
                TestModelEvent testModelEvent = new TestModelEvent(studentModel);
                sendEvent(testModelEvent);
            }
            else if(studentModel.getStatus() == Model.Status.Tested) {
                PublishResultsEvent publishReEve = new PublishResultsEvent(studentModel);
                sendEvent(publishReEve);
            }
            student.getModelQueue().add(studentModel);
        });
        /**
         * all the model that are in this linkList the student "read"
         * count all the model that are belongs to this student and add them to his publications
         */
        subscribeBroadcast(PublishConferenceBroadcast.class,(PublishConferenceBroadcast publishB)->{
            LinkedList<Model> confList = publishB.getConfrenceInformation().getConInfo();
            student.setPaperRead(confList.size());
            int publications = 0;
            for(Model model : confList){
                if(student == model.getStudent())
                    publications++;
            }
        });
    }
}
