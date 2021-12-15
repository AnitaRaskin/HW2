package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Terminated;
import bgu.spl.mics.application.objects.GPU;

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
    private String name;

    public GPUService(GPU gpu) {
        super("GPUService");
        this.gpu = gpu;
    }

    @Override
    protected void initialize() {
        Callback<Terminated> terminatedGPU = terminated -> {
            this.terminate();
        };
        callbackEvent.put(Terminated.class, terminatedGPU);

    }
}
