import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;

public class Part2 {
	private static final int NUM_GENERATOR = 10;
	private static final int NUM_PROCESSOR = 3;

	public static void main(String[] args) {
		PreProcessorSynch preProcessor;
		Queue<String> preprocessQueue = new LinkedList<String>();
		DataGeneratorSynch[] generators = new DataGeneratorSynch[NUM_GENERATOR];
		ArrayList< Deque<Integer> > outputQueues = new ArrayList<Deque<Integer>>();

		ForkJoinPool mergeFJPool = new ForkJoinPool();
		DataProcessorFJ[] processors = new DataProcessorFJ[NUM_PROCESSOR];

		System.out.println("Main thread started");

		for(int i = 0; i < NUM_GENERATOR; i++) {
			generators[i] = new DataGeneratorSynch(preprocessQueue, i);
			outputQueues.add(new LinkedList<Integer>());
		}
		for (int i = 0; i < NUM_PROCESSOR; i++)
			processors[i] = new DataProcessorFJ(mergeFJPool, outputQueues, i, NUM_GENERATOR);
		preProcessor = new PreProcessorSynch(preprocessQueue, outputQueues);

		(new Thread(preProcessor)).start();
		for (int i = 0; i < NUM_GENERATOR; i++)
			(new Thread(generators[i])).start();
		for (int i = 0; i < NUM_PROCESSOR; i++)
			(new Thread(processors[i])).start();
	}
}

