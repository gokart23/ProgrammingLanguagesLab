/*
*	CS431: Programming Languages Lab
*	Assignment 1,	Part 1: Segment 1
*
*	Karthik Duddu, Mohit Chhajed
*	Group 25
*/

import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;

/**
*	Class to process data using Runnable and synchronized
*/
public class DataProcessorSynch implements Runnable {
	// Threshold values of all 3 operations
	//	Index 0: Average, 1: Multiplication, 2: Sum
	private static final int[] THRESHOLD = {100, 100000, 10000};

	// Variable MODE stores operation performed by thread (0, 1 or 2)
	// Variable NUM_GENERATOR stores the number of generator queues to process
	private final int MODE, NUM_GENERATOR;
	private final ArrayList<Deque<Integer>> outputQueues;

	// State variables of the object, used for processing data and identifying the processor
	private int output, cycle;

	// Constructor: Sets state variables, MODE of the thread (avg/multiply/sum) and output queue references
	public DataProcessorSynch(ArrayList<Deque<Integer>> outputQueues, int mode, int num_generator) {
		this.outputQueues = outputQueues;
		this.MODE = mode;
		this.cycle = 0;
		this.NUM_GENERATOR = num_generator;
	}

	// Function to continuously process data and sleep for approximately 1000ms 
	//	Data processed in a new thread according to mode of the object
	public void run() {
		if (MODE == 0)		mean();
		else if (MODE == 1)	multiply();
		else				add();
	}

	// Function for obtaining values from a particular output queue in a thread-safe manner
	//	Uses synchronization on output queue object to achieve thread-safety
	//	Return value: Returns topmost element of queue if exists and not accessed, otherwise -1
	private int getValSynchronized(int idx) {
		// State variables for returning results
		int count=0, res=-1;
	
		// Synchronization and thread safety achieved by synchronizing on queue during access
		synchronized(outputQueues.get(idx)) {
			// Check if queue has an element to read
			if (outputQueues.get(idx).size() > 0) {
	
				count = outputQueues.get(idx).removeFirst();
				res = outputQueues.get(idx).removeFirst();				

				// Check if element has already been accessed by this thread
				//	If yes, set return value to -1. Else, set the element as accessed
				if ( (count & (1 << MODE)) == 0)
					count = count | (1 << MODE);									
				else res = -1;

				// Check if all elements have accessed this element.
				//	If yes, remove element from top of queue. Else, keep element
				if ( (count+1) != (1 << 3) ) {
					outputQueues.get(idx).addFirst(res);
					outputQueues.get(idx).addFirst(count);
				}
			}
		}
		// Released synchronization before exit
		return res;
	} 

	// Function to compute average of all output queue values, and compare against threshold
	private void mean() {
		// State variables to compute sum and store values fetched from queue
		long sum=0;
		Integer[] vals = new Integer[NUM_GENERATOR];
		
		while (true) {
			sum=0;
			try {
					// Retrieve values from all output queues
					// 	If not available at some point, wait until available
					for (int i = 0; i < NUM_GENERATOR; ) {
						vals[i] = getValSynchronized(i);
						if (vals[i] >= 0) i++;						
					}

					// Calculate sum and output information
					String op = "AVG: #" + (cycle++) + "[";
					for (Integer val : vals) {
						sum += val;
						op += val + ", ";						
					}
					sum /= NUM_GENERATOR;
					op += "] - AVG=" + sum + " ";

					// Compare with threshold and check if greater
					//	If yes, output state detection. Else, output absence of state
					if ( sum >= THRESHOLD[MODE] )
						op += ":State detected from (avg)";
					else
						op += ":State not detected from (avg)";

					// Print output information and sleep for 1000ms
					System.out.println(op);
					Thread.sleep(1000);

			} catch (Exception e) { e.printStackTrace(); }
		}	
	}

	// Function to compute thresholded multiplication of all output queue values
	// and compare against threshold
	private void multiply() {
		// State variables to compute sum and store values fetched from queue
		long prod=1;
		Integer[] vals = new Integer[NUM_GENERATOR];

		while (true) {
			prod=1;
			try {
					// Retrieve values from all output queues
					// 	If not available at some point, wait until available
					for (int i = 0; i < NUM_GENERATOR; ) {
						vals[i] = getValSynchronized(i);
						if (vals[i] >= 0) i++;						
					}

					// Calculate multiplication and output information
					//	Note: To avoid overflows, if product already exceeds threshold, product calculation is skipped
					String op = "MUL: #" + (cycle++) + "[";
					for (Integer val : vals) {
						if (prod < THRESHOLD[MODE] || val == 0 )
							prod *= val;
						op += val + ", ";						
					}
					op += "] - ThresholdProd=" + prod + " ";

					// Compare with threshold and check if greater
					//	If yes, output state detection. Else, output absence of state
					if ( prod >= THRESHOLD[MODE] )
						op += ":State detected from (product)";
					else
						op += ":State not detected from (product)";

					// Print output information and sleep for 1000ms
					System.out.println(op);
					Thread.sleep(1000);

			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	// Function to compute sum of all output queue values, and compare against threshold
	private void add() {
		// State variables to compute sum and store values fetched from queue
		long sum=0;
		Integer[] vals = new Integer[NUM_GENERATOR];

		while (true) {
			sum=0;
			try {
					// Retrieve values from all output queues
					// 	If not available at some point, wait until available
					for (int i = 0; i < NUM_GENERATOR; ) {
						vals[i] = getValSynchronized(i);
						if (vals[i] >= 0) i++;						
					}

					// Calculate sum and output information
					String op = "SUM: #" + (cycle++) + "[";
					for (Integer val : vals) {
						sum += val;
						op += val + ", ";						
					}
					op += "] - Sum=" + sum + " ";

					// Compare with threshold and check if greater
					//	If yes, output state detection. Else, output absence of state
					if ( sum >= THRESHOLD[MODE] )
						op += ":State detected from (sum)";
					else
						op += ":State not detected from (sum)";

					// Print output information and sleep for 1000ms
					System.out.println(op);
					Thread.sleep(1000);

			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}