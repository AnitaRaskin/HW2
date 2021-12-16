package bgu.spl.mics;

import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.LinkedList;

public class PublishConferenceBroadcast implements Broadcast{

    private ConfrenceInformation confrenceInformation;

    public PublishConferenceBroadcast(ConfrenceInformation confrenceInformation){
        this.confrenceInformation = confrenceInformation;

    }

    public ConfrenceInformation getConfrenceInformation() {
        return confrenceInformation;
    }
}
