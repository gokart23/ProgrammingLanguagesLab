import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;

public class Part1_1 {
	private static final int NUM_GENERATOR = 10;
	private static final int NUM_PROCESSOR = 3;

	public static void main(String[] args) {
		PreProcessor preProcessor;
		Queue<String> preprocessQueue = new LinkedList<String>();
		DataProcessor[] processors = new DataProcessor[NUM_PROCESSOR];
		DataGenerator[] generators = new DataGenerator[NUM_GENERATOR];
		ArrayList< Deque<Integer> > outputQueues = new ArrayList<Deque<Integer>>();

		System.out.println("Main thread started");

		for(int i = 0; i < NUM_GENERATOR; i++) {
			generators[i] = new DataGenerator(preprocessQueue, i);
			outputQueues.add(new LinkedList<Integer>());
		}
		for (int i = 0; i < NUM_PROCESSOR; i++)
			processors[i] = new DataProcessor(outputQueues, i, NUM_GENERATOR);
		preProcessor = new PreProcessor(preprocessQueue, outputQueues);

		(new Thread(preProcessor)).start();
		for (int i = 0; i < NUM_PROCESSOR; i++)
			(new Thread(processors[i])).start();
		for (int i = 0; i < NUM_GENERATOR; i++)
			(new Thread(generators[i])).start();
	}
}

