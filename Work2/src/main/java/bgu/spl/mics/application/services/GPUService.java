package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.GPU;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    //Fields
    private GPU gpu;
    private Queue<TestModelEvent> testModelEventQueue;
    private Queue<TrainModelEvent> trainModelEventQueue;
    private TrainModelEvent currentEV = null;
    private String name;

    public GPUService(GPU gpu) {
        super("GPUService");
        this.gpu = gpu;
        testModelEventQueue = new LinkedList<>();
        trainModelEventQueue = new LinkedList<>();
    }

    /**
     * this function test all the test events that are existed to this gpu
     * and then assign the gpu with train model only if it's possible
     */
    private void updateTheEvent(){
        while(testModelEventQueue != null){
            TestModelEvent testModelEve = testModelEventQueue.poll();
            gpu.updateModel(testModelEve.getModel());
            gpu.testModel();
            complete(testModelEve,testModelEve.getModel());
            testModelEve.getModel().updateStatus();
        }
        if(trainModelEventQueue == null)
            gpu.updateModel(null);
        else{
            TrainModelEvent trainModelEve = trainModelEventQueue.poll();
            currentEV = trainModelEve;
            gpu.updateModel(trainModelEve.getModel());
            gpu.splitDataToBatches();
            gpu.sendDataToPro();
            gpu.doTick();
            trainModelEve.getModel().updateStatus();
        }
    }
    @Override
    protected void initialize() {
        subscribeBroadcast(Terminated.class, (Terminated terminated)->{
            terminate();
        });
        /**
         * check if the gpu is in the middle of training model
         * if yes only add him tick otherwise also change the model
         * if there are no more available models he gives him null model
         */
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast timeB)->{
            if(gpu.getModel()==null){
                updateTheEvent();
            }
            else {
                if(gpu.getModel().getData().dataTrained()){
                    complete(currentEV,currentEV.getModel());
                    updateTheEvent();
                }
                gpu.doTick();
            }
        });
        subscribeEvent(TrainModelEvent.class,(TrainModelEvent trainModelEvent)->{
            trainModelEventQueue.add(trainModelEvent);
        });
        subscribeEvent(TestModelEvent.class,(TestModelEvent testModelEven)->{
            testModelEventQueue.add(testModelEven);
        });
    }
}
