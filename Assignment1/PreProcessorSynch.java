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
*	Class to preprocess data by converting the strings to integers
*/
class PreProcessorSynch implements Runnable {

	// Variable preprocessQueue stores reference to the preprocessing queue
	// Variable outputQueue stores reference to the output queue
	private final Queue<String> preprocessQueue;
	private final Deque<Integer> outputQueue;

	// State variables for converting the queue input to integers
	private String inpString;
	private int qNum;

	// Constructor: Sets state variables, input and output queue references
	public PreProcessorSynch(Queue<String> preprocessQueue, Deque<Integer> outputQueue) {
		this.preprocessQueue = preprocessQueue;
		this.outputQueue = outputQueue;
		this.inpString = "";
		this.qNum = -1;
	}

	// Function to continuously convert input strings to output integers in a thread safe manner
	//	Thread safety implemented using BlockedQueue and Lock from java.util.concurrent package
	public void run() {
		while (true) {
			// Obtain top of queue in a thread-safe manner by using synchronized keyword
			synchronized(preprocessQueue) {
				//	If top of queue is empty, then return
				if (preprocessQueue.size() > 0) {
					inpString = preprocessQueue.remove();
					qNum = Integer.parseInt(preprocessQueue.remove());
				}
				else
					qNum = -1;
			}
			// Release preprocessing queue for use by other threads
			// If new value from top of queue exists, then proceeed. Else, return
			if (qNum != -1) {
				try {
					// Synchronize on the output queue
					synchronized(outputQueue) {
						outputQueue.addLast(0);
						outputQueue.addLast( Integer.parseInt(inpString, 2) );
					}
					// Release output queue locks to allow other threads to access
				} catch (Exception e) { e.printStackTrace(); }
			}

		}

	}
}
