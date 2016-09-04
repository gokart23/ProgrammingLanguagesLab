import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;

class PreProcessorSynch implements Runnable {
	private final Queue<String> preprocessQueue;
	private final ArrayList<Deque<Integer>> outputQueues;
	private String inpString;
	private int qNum;

	public PreProcessorSynch(Queue<String> preprocessQueue, ArrayList<Deque<Integer>> outputQueues) {
		this.preprocessQueue = preprocessQueue;
		this.outputQueues = outputQueues;
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
					synchronized(outputQueues.get(qNum)) {
						outputQueues.get(qNum).addLast(0);
						outputQueues.get(qNum).addLast( Integer.parseInt(inpString, 2) );
						// System.out.println( outputQueues.get(0).size() + " " + outputQueues.get(1).size() );
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
		}
	}
}
