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
	private static Queue<DataBatch> dataFromGPU;
	private static Object lockCPU = new Object();
	private static Object lockGPU = new Object();
	private static Object lockReturnDG = new Object();



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
	public void addCPU(CPU cpu){
		synchronized (lockCPU) {
			if (dataFromGPU != null) {
				cpu.receiveData(dataFromGPU.poll());
			}
			else {
				CPUS.add(cpu);
			}
		}
	}
	public void addGPU(GPU gpu){
		GPUS.add(gpu);
	}

	/**
	 * in this function the GPU give the cluster unprocessed data so the cpu can process it
	 * the cluster give the dataBatch only for cpu that is not working if it doesn't have someone free
	 * we save the dataBatch for the first cpu that going to be free
	 * @param dataBatch = the dataBatch that we want to process
	 */
	public void takeDataToProc(DataBatch dataBatch){
		synchronized (lockGPU) {
			if (CPUS != null) {
				CPU currentCPU = CPUS.remove();
				currentCPU.receiveData(dataBatch);
				//CPUS.add(currentCPU);
			} else {
				dataFromGPU.add(dataBatch);
			}
		}
	}

	/**
	 * with function the cpu use after it finish to process data
	 * and now we want to return this dataBatch to the GPU that the dataBatch send from;
	 * @param dataBatch
	 */
	public void sendProcessedData(DataBatch dataBatch){
		synchronized (lockReturnDG){
			Data originOfData = dataBatch.getData();
			boolean found = false;
			for(int i=0; i<= GPUS.size() && !found; i++){
				GPU currentGPU = GPUS.poll();
				if(currentGPU.getModel().getData()==originOfData){
					found = true;
					currentGPU.addProcessedData(dataBatch);
				}
			}
		}
	}
}
