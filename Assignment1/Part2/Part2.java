/*
*	CS431: Programming Languages Lab
*	Assignment 1,	Part 2
*
*	Karthik Duddu, Mohit Chhajed
*	Group 25
*/

import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;

/**
*	Stub Class to initialize data resources and start threads
*/
public class Part2 {
	// Variables storing number of generator and processor threads
	private static final int NUM_GENERATOR = 10;
	private static final int NUM_PROCESSOR = 3;

	// Function to initialize data resources and start threads
	public static void main(String[] args) {
		// Declaration of data resources (queues)
		ArrayList< Deque<Integer> > outputQueues = new ArrayList<Deque<Integer>>();
		ArrayList< Queue<String> > preprocessQueues = new ArrayList< Queue<String> >();
		
		// Declaration of thread resources (in this case Executor thread pools)
		PreProcessorSynch[] preProcessors = new PreProcessorSynch[NUM_GENERATOR];
		DataGeneratorSynch[] generators = new DataGeneratorSynch[NUM_GENERATOR];
		DataProcessorFJ[] processors = new DataProcessorFJ[NUM_PROCESSOR];

		// Declaration of fork-join thread pool
		ForkJoinPool mergeFJPool = new ForkJoinPool();

		System.out.println("Main thread started");

		// Initialization of data resources (queues)
		// Initialization of generator and preprocessor thread resources
		for(int i = 0; i < NUM_GENERATOR; i++) {
			outputQueues.add(new LinkedList<Integer>());
			preprocessQueues.add(new LinkedList<String>());
			generators[i] = new DataGeneratorSynch(preprocessQueues.get(i), i);
			preProcessors[i] = new PreProcessorSynch(preprocessQueues.get(i), outputQueues.get(i));
		}
		
		// Initialization of processor thread resources
		for (int i = 0; i < NUM_PROCESSOR; i++)
			processors[i] = new DataProcessorFJ(mergeFJPool, outputQueues, i, NUM_GENERATOR);

		// Starting of generator and preprocessing threads		
		for (int i = 0; i < NUM_GENERATOR; i++) {
			(new Thread(preProcessors[i])).start();
			(new Thread(generators[i])).start();
		}
		
		// Starting of processor threads
		for (int i = 0; i < NUM_PROCESSOR; i++)
			(new Thread(processors[i])).start();
	}
}

