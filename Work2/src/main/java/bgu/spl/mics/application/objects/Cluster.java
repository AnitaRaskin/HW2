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
	private static int dataBatchSize;
	private static Queue<CPU> CPUS;
	private static Queue<GPU> GPUS;
	private static Queue<DataBatch> dataFromGPUTabular;
	private static Queue<DataBatch> dataFromGPUText;
	private static Queue<DataBatch> dataFromGPUImages;
	//private static Queue<CPU> awailableCPU;
	private static Object lockCPU = new Object();
	private static Object lockGPU = new Object();
	private static Object lockReturnDG = new Object();



	/**
     * Retrieves the single instance of this class.
     */
	public static synchronized Cluster getInstance() {
		//TODO: Implement this
		if(thisCluster == null){
			thisCluster = new Cluster();
		}
		return thisCluster;
	}
	private Cluster (){
		CPUS = new LinkedList<CPU>();
		GPUS = new LinkedList<GPU>();

		dataBatchSize = 0;
		dataFromGPUTabular = new LinkedList<>();
		dataFromGPUText = new LinkedList<>();
		dataFromGPUImages = new LinkedList<>();
		//awailableCPU = new LinkedList<>();
	}

	public void addCPU(CPU cpu){
		//awailableCPU.add(cpu);
		synchronized (lockCPU) {
			if (dataFromGPUTabular.size() > 0) {
				cpu.receiveData(dataFromGPUTabular.poll());
				System.out.println("CPU take dataBatch from Tabular Cluster 52");
			}
			else if(dataFromGPUText.size() > 0){
				cpu.receiveData(dataFromGPUText.poll());
				System.out.println("CPU take dataBatch from Text Cluster 52");
			}
			else if(dataFromGPUImages.size() > 0){
				cpu.receiveData(dataFromGPUImages.poll());
				System.out.println("CPU take dataBatch from Images Cluster 52");
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
			if (CPUS.size() > 0) {
				CPU currentCPU = bestCPU(CPUS,dataBatch);
				if (currentCPU != null) {
					currentCPU.receiveData(dataBatch);
					System.out.println("CPU " + currentCPU + " is free Cluster 68");
				}
				//CPUS.add(currentCPU);
			} else {// have no CPU to take this data
				if(dataBatch.getType() == Data.Type.Text)
					dataFromGPUText.add(dataBatch);
				else if(dataBatch.getType() == Data.Type.Tabular)
					dataFromGPUTabular.add(dataBatch);
				else
					dataFromGPUImages.add(dataBatch);
//				System.out.println("I have no CPU free Cluster 72");
			}
		}
	}

	/**
	 * this function search for the best CPU available to do the process
	 * @param cpus
	 * @param dataBatch
	 * @return the best CPU
	 */
	private CPU bestCPU(Queue<CPU> cpus, DataBatch dataBatch){
		CPU bestCPU = cpus.peek();
		for (int i=0; i< cpus.size(); i++){
			CPU temp = cpus.poll();
			if( bestCPU != null && temp != null && processingTime(bestCPU,dataBatch.getType()) > processingTime(temp, dataBatch.getType()))
				bestCPU = temp;
			cpus.add(temp);
		}
		boolean found = false;
		for (int i=0; i< cpus.size() && !found; i++){
			CPU current = cpus.poll();
			if(current==bestCPU)
				found=true;
			else
				cpus.add(current);
		}
		return bestCPU;
	}
	/**
	 *
	 * this function calculate the time need to process this dataBatch
	 * @param type
	 * @return
	 */
	private int processingTime(CPU cpu, Data.Type type){
		if(type == Data.Type.Images){//Images
			return (32/cpu.getCoresNum())*4;
		}
		else if(type == Data.Type.Text){//Text
			return (32/cpu.getCoresNum())*2;
		}
		else{//Tabular
			return (32/cpu.getCoresNum())*1;
		}
	}
	/**
	 * with function the cpu use after it finish to process data
	 * and now we want to return this dataBatch to the GPU that the dataBatch send from;
	 * @param dataBatch
	 */
	public void sendProcessedData(DataBatch dataBatch){
		System.out.println("sending data back to GPU");
		synchronized (lockReturnDG){
			Data originOfData = dataBatch.getData();
			dataBatchSize = dataBatchSize + 1;
			boolean found = false;
			for(int i=0; i< GPUS.size() && !found; i++){
				GPU currentGPU = GPUS.poll();
				if(currentGPU.getModel()!=null && currentGPU.getModel().getData()==originOfData){
					found = true;
					currentGPU.addProcessedData(dataBatch);
					System.out.println("VRAM succeed to return Data Cluster142");
				}
				GPUS.add(currentGPU);
			}
		}
	}
	public int getDataBatchSize(){
		return dataBatchSize;
	}
}
