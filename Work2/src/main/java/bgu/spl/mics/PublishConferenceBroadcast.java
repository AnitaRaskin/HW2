package bgu.spl.mics;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * class that implements Broadcast
 * should send a broadcast to all the students with a list of all the Models that were Trained until now and got the result of "Good"
 */
public class PublishConferenceBroadcast implements Broadcast{
    //Fields
    private ConfrenceInformation confrenceInformation;

    /**
     *
     * Constructor
     * @param confrenceInformation that should send a broadcast to all the students at this date
     */
    public PublishConferenceBroadcast(ConfrenceInformation confrenceInformation){
        this.confrenceInformation = confrenceInformation;

    }

    /**
     *
     * @return an object type ConfrenceInformation that stores all the Good models.
     */
    public ConfrenceInformation getConfrenceInformation() {
        return confrenceInformation;
    }
}
