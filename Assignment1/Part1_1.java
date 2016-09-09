import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;

public class Part1_1 {
	private static final int NUM_GENERATOR = 10;
	private static final int NUM_PROCESSOR = 3;

	public static void main(String[] args) {
		PreProcessorSynch[] preProcessors = new PreProcessorSynch[NUM_GENERATOR];
		DataProcessorSynch[] processors = new DataProcessorSynch[NUM_PROCESSOR];
		DataGeneratorSynch[] generators = new DataGeneratorSynch[NUM_GENERATOR];
		ArrayList< Deque<Integer> > outputQueues = new ArrayList<Deque<Integer>>();
		ArrayList< Queue<String> > preprocessQueues = new ArrayList<Queue<String>>();

		System.out.println("Main thread started");

		for(int i = 0; i < NUM_GENERATOR; i++) {
			preprocessQueues.add(new LinkedList<String>());
			outputQueues.add(new LinkedList<Integer>());
			generators[i] = new DataGeneratorSynch(preprocessQueues.get(i), i);
			preProcessors[i] = new PreProcessorSynch(preprocessQueues.get(i), outputQueues.get(i));
		}
		for (int i = 0; i < NUM_PROCESSOR; i++)
			processors[i] = new DataProcessorSynch(outputQueues, i, NUM_GENERATOR);
		
		for (int i = 0; i < NUM_GENERATOR; i++) {
			(new Thread(preProcessors[i])).start();
			(new Thread(generators[i])).start();
		}
		for (int i = 0; i < NUM_PROCESSOR; i++)
			(new Thread(processors[i])).start();
	}
}