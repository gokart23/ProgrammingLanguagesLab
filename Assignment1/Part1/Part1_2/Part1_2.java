/*
*	CS431: Programming Languages Lab
*	Assignment 1,	Part 1: Segment 2
*
*	Karthik Duddu, Mohit Chhajed
*	Group 25
*/

import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.*;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

/**
*	Stub Class to initialize data resources and start threads
*/
public class Part1_2 {
	// Variables storing number of generator and processor threads
	private static final int NUM_GENERATOR = 10;
	private static final int NUM_PROCESSOR = 3;

	// Function to initialize data resources and start threads
	public static void main(String[] args) {
		// Declaration of data resources (queues)
		ArrayList< Deque<Integer> > outputQueues = new ArrayList< Deque<Integer> >();
		ArrayList< BlockingQueue<String> > preprocessQueues = new ArrayList< BlockingQueue<String> >();
		
		// Declaration of synchronization resources (in this case, locks)
		Lock[] outputLocks = new Lock[NUM_GENERATOR];

		// Declaration of thread resources (in this case Executor thread pools)
		ExecutorService processors = Executors.newFixedThreadPool(NUM_PROCESSOR);
		ExecutorService generators = Executors.newFixedThreadPool(NUM_GENERATOR);
		ExecutorService preProcessors = Executors.newFixedThreadPool(NUM_GENERATOR);

		System.out.println("Main thread started.");

		// Initialization of data resources (queues)
		// Initialization of generator and preprocessor thread resources
		for(int i = 0; i < NUM_GENERATOR; i++) {
			outputLocks[i] = new ReentrantLock();
			outputQueues.add(new LinkedList<Integer>());
			preprocessQueues.add(new LinkedBlockingDeque<String>());
			preProcessors.submit(new PreProcessorConc(preprocessQueues.get(i), outputQueues.get(i), outputLocks[i]));
			generators.submit(new DataGeneratorConc(preprocessQueues.get(i), i));
		}

		// Initialization of processor thread resources
		for (int i = 0; i < NUM_PROCESSOR; i++)
			processors.submit(new DataProcessorConc(outputQueues, outputLocks, i, NUM_GENERATOR));

		// Releasing all Executor resources
		processors.shutdown();
		generators.shutdown();
		preProcessors.shutdown();
	}
}

