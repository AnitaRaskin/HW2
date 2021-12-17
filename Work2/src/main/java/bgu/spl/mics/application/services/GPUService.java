package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.GPU;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link }.
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
        while(testModelEventQueue.size() != 0){ //COULD STACK ALL THE PROGRAM
            TestModelEvent testModelEve = testModelEventQueue.poll();
            gpu.updateModel(testModelEve.getModel());
            gpu.testModel();
            complete(testModelEve, testModelEve.getModel());
            testModelEve.getModel().updateStatus();
        }
        if(trainModelEventQueue.size() == 0){
            gpu.updateModel(null);
            currentEV = null;
        }
        else{
            TrainModelEvent trainModelEve = trainModelEventQueue.poll();
            currentEV = trainModelEve;
            gpu.updateModel(trainModelEve.getModel());
            gpu.splitDataToBatches();
            gpu.sendDataToPro();
            trainModelEve.getModel().updateStatus();
        }
    }
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast terminated)->terminate());

        /**
         * check if the gpu is in the middle of training model
         * if yes only add him tick otherwise also change the model
         * if there are no more available models he gives him null model
         */
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast timeB)->{
            if(currentEV == null){
                updateTheEvent();
                if(currentEV != null)
                    gpu.doTick();
            }
            else {
                gpu.doTick();
                if(gpu.getModel().getData().dataTrained()){
                    complete(currentEV,currentEV.getModel());//WHY MODEL AND NOT RESULT
                    updateTheEvent();
                }
            }
        });
        subscribeEvent(TrainModelEvent.class,(TrainModelEvent trainModelEvent)->{
            System.out.println("got shit to train GPUS-82");
            trainModelEventQueue.add(trainModelEvent);
            if(currentEV == null)
                updateTheEvent();
        });
        subscribeEvent(TestModelEvent.class,(TestModelEvent testModelEven)-> {
            testModelEventQueue.add(testModelEven);
            if(currentEV == null)
                updateTheEvent();
        });
    }
}
