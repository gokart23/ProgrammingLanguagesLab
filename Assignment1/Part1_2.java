import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.*;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;


public class Part1_2 {
	private static final int NUM_GENERATOR = 10;
	private static final int NUM_PROCESSOR = 3;

	public static void main(String[] args) {
		Lock[] outputLocks = new Lock[NUM_GENERATOR];
		ExecutorService processors = Executors.newFixedThreadPool(NUM_PROCESSOR);
		ExecutorService generators = Executors.newFixedThreadPool(NUM_GENERATOR);
		ExecutorService preProcessors = Executors.newFixedThreadPool(NUM_GENERATOR);
		ArrayList< Deque<Integer> > outputQueues = new ArrayList< Deque<Integer> >();
		ArrayList< BlockingQueue<String> > preprocessQueues = new ArrayList< BlockingQueue<String> >();

		System.out.println("Main thread started");

		for(int i = 0; i < NUM_GENERATOR; i++) {
			outputLocks[i] = new ReentrantLock();
			outputQueues.add(new LinkedList<Integer>());
			preprocessQueues.add(new LinkedBlockingDeque<String>());
			preProcessors.submit(new PreProcessorConc(preprocessQueues.get(i), outputQueues.get(i), outputLocks[i]));
			generators.submit(new DataGeneratorConc(preprocessQueues.get(i), i));
		}
		for (int i = 0; i < NUM_PROCESSOR; i++)
			processors.submit(new DataProcessorConc(outputQueues, outputLocks, i, NUM_GENERATOR));

		processors.shutdown();
		generators.shutdown();
		preProcessors.shutdown();
	}
}

