import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;

public class Part1_1 {
	static final int NUM_GENERATOR = 10;
	static final int NUM_PROCESSOR = 3;
	public static void main(String[] args) {
		PreProcessor preProcessor;
		Queue<String> preprocessQueue = new LinkedList<String>();
		DataGenerator[] generators = new DataGenerator[NUM_GENERATOR];
		ArrayList< Deque<Integer> > outputQueues = new ArrayList<Deque<Integer>>();

		System.out.println("Main thread started");

		for(int i = 0; i < NUM_GENERATOR; i++) {
			generators[i] = new DataGenerator(preprocessQueue, i);
			outputQueues.add(new LinkedList<Integer>());
		}
		preProcessor = new PreProcessor(preprocessQueue, outputQueues);

		(new Thread(preProcessor)).start();
		for (int i = 0; i < NUM_GENERATOR; i++)
			(new Thread(generators[i])).start();
	}
}

