package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.CPU;

/**
 * CPU service is responsible for handling the {@link }.
 * This class may not hold references for objects which it is not responsible for.
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    //Fields
    private CPU cpu;
    private String name;

    public CPUService(CPU cpu) {
        super("CPUService");
        this.cpu = cpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (terminate) -> {
            this.terminate();});

        subscribeBroadcast(TickBroadcast.class, (tick) -> this.cpu.updateTime());
    }
}
