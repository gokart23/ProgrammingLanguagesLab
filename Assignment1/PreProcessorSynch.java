import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;

class PreProcessorSynch implements Runnable {
	private final Queue<String> preprocessQueue;
	private final Deque<Integer> outputQueue;
	private String inpString;
	private int qNum;

	public PreProcessorSynch(Queue<String> preprocessQueue, Deque<Integer> outputQueue) {
		this.preprocessQueue = preprocessQueue;
		this.outputQueue = outputQueue;
		this.inpString = "";
		this.qNum = -1;
	}

	public void run() {
		while (true) {
			synchronized(preprocessQueue) {
				if (preprocessQueue.size() > 0) {
					inpString = preprocessQueue.remove();
					qNum = Integer.parseInt(preprocessQueue.remove());
				}
				else
					qNum = -1;
			}
			if (qNum != -1) {
				try {
					synchronized(outputQueue) {
						outputQueue.addLast(0);
						outputQueue.addLast( Integer.parseInt(inpString, 2) );
						// System.out.println( outputQueues.get(0).size() + " " + outputQueues.get(1).size() );
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
	}
}
