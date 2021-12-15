package bgu.spl.mics;

public class
TickBroadcast implements Broadcast{

    private boolean terminate;
    public TickBroadcast(boolean terminate){
        this.terminate = terminate;
    }

    public boolean needToTerminate() {
        return terminate;
    }
}
