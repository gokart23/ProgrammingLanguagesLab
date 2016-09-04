import java.util.Deque;
import java.util.ArrayList;
import java.util.concurrent.locks.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

class PreProcessorConc implements Runnable {
	private final BlockingQueue<String> preprocessQueue;
	private final Lock[] outputLocks;
	private final ArrayList<Deque<Integer>> outputQueues;
	private String inpString;
	private int qNum;

	public PreProcessorConc(BlockingQueue<String> preprocessQueue, ArrayList<Deque<Integer>> outputQueues, Lock[] outputLocks) {
		this.preprocessQueue = preprocessQueue;
		this.outputQueues = outputQueues;
		this.outputLocks = outputLocks;
		this.inpString = "";
		this.qNum = -1;
	}

	public void run() {
		while (true) {
			try{
				inpString = preprocessQueue.take();
				qNum = inpString.charAt(0) - '0';
				outputLocks[qNum].lock();
					outputQueues.get(qNum).addLast(0);
					outputQueues.get(qNum).addLast( Integer.parseInt(inpString.substring(1), 2) );
				outputLocks[qNum].unlock();
				// System.out.println( outputQueues.get(0).size() + " " + outputQueues.get(1).size() );								
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

}
