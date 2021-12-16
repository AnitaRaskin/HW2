package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    //Fields
    private ConfrenceInformation confrenceInformation;
    private String name;

    /**
     *
     * Constructor
     * @param confrenceInformation
     */
    public ConferenceService(ConfrenceInformation confrenceInformation) {
        super("conferenceInformation");
        this.confrenceInformation = confrenceInformation;
    }

    @Override
    protected void initialize() {
        //basic subscribe - terminate and tickBroadcast
        subscribeBroadcast(Terminated.class, (terminate) -> this.terminate());
        subscribeBroadcast(TickBroadcast.class, (tick) -> {
            this.confrenceInformation.doTick();
            /**
             *
             * PublishConferenceBroadcast:
             * a. broadcast all the GOOD models at a set time -> to the students
             *    set time: if(time == date)
             * b. unregister
             */
            if(confrenceInformation.getTick() == confrenceInformation.getDate()){
                sendBroadcast(new PublishConferenceBroadcast(confrenceInformation));
                terminate();
            }
        });

        /**
         *
         * PublishResultsEvent:
         * will add to confrenceInformation the "Good" model the studend send him
         * add to the linkedlist confrenceInformation
         */
        subscribeEvent(PublishResultsEvent.class, (addModel) -> confrenceInformation.addInfo(addModel.getModel()));



    }

}
