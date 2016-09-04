import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class Part1_2 {
	private static final int NUM_GENERATOR = 3;
	private static final int NUM_PROCESSOR = 3;

	public static void main(String[] args) {
		PreProcessorConc preProcessor;
		BlockingQueue<String> preprocessQueue = new LinkedBlockingDeque<String>();
		DataProcessorConc[] processors = new DataProcessorConc[NUM_PROCESSOR];
		DataGeneratorConc[] generators = new DataGeneratorConc[NUM_GENERATOR];
		Lock[] outputLocks = new Lock[NUM_GENERATOR];
		ArrayList< Deque<Integer> > outputQueues = new ArrayList< Deque<Integer> >();

		System.out.println("Main thread started");

		for(int i = 0; i < NUM_GENERATOR; i++) {
			generators[i] = new DataGeneratorConc(preprocessQueue, i);
			outputQueues.add(new LinkedList<Integer>());
			outputLocks[i] = new ReentrantLock();
		}
		for (int i = 0; i < NUM_PROCESSOR; i++)
			processors[i] = new DataProcessorConc(outputQueues, outputLocks, i, NUM_GENERATOR);
		preProcessor = new PreProcessorConc(preprocessQueue, outputQueues, outputLocks);

		(new Thread(preProcessor)).start();
		for (int i = 0; i < NUM_GENERATOR; i++)
			(new Thread(generators[i])).start();
		for (int i = 0; i < NUM_PROCESSOR; i++)
			(new Thread(processors[i])).start();
	}
}

