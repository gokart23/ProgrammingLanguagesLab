import java.util.Deque;
import java.util.ArrayList;
import java.util.concurrent.locks.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

class PreProcessorConc implements Runnable {
	private final Lock outputLock;
	private final BlockingQueue<String> preprocessQueue;
	private final Deque<Integer> outputQueue;
	private String inpString;
	private int qNum;

	public PreProcessorConc(BlockingQueue<String> preprocessQueue, Deque<Integer> outputQueue, Lock outputLock) {
		this.preprocessQueue = preprocessQueue;
		this.outputQueue = outputQueue;
		this.outputLock = outputLock;
		this.inpString = "";
		this.qNum = -1;
	}

	public void run() {
		while (true) {
			try{
				inpString = preprocessQueue.take();
				qNum = inpString.charAt(0) - '0';
				outputLock.lock();
					outputQueue.addLast(0);
					outputQueue.addLast( Integer.parseInt(inpString.substring(1), 2) );
				outputLock.unlock();
				// System.out.println( outputQueues.get(0).size() + " " + outputQueues.get(1).size() );								
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

}
