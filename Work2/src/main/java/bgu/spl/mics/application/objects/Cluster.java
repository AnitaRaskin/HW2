package bgu.spl.mics.application.objects;


import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	//field
	private static Cluster thisCluster = null;
	private static Queue<CPU> CPUS;
	private static Queue<GPU> GPUS;
	private static Queue<DataBatch> dataFromGPU;//why Anita??
	private static Object lockCPU = new Object();



	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		//TODO: Implement this
		if(thisCluster == null){
			thisCluster = new Cluster();
		}
		return thisCluster;
	}
	private Cluster (){
		GPUS = new LinkedList<GPU>();
		CPUS = new LinkedList<CPU>();
	}
	public void takeDataToProc(DataBatch dataBatch){
		synchronized (lockCPU) {
			CPU currentCPU = CPUS.remove();
			currentCPU.processData();
			CPUS.add(currentCPU);
		}
	}
	public void sendData(DataBatch dataBatch){


	}
}
