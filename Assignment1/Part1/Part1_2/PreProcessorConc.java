/*
*	CS431: Programming Languages Lab
*	Assignment 1,	Part 1: Segment 2
*
*	Karthik Duddu, Mohit Chhajed
*	Group 25
*/

import java.util.Deque;
import java.util.ArrayList;
import java.util.concurrent.locks.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
*	Class to preprocess data by converting the strings to integers
*/
class PreProcessorConc implements Runnable {

	// Variable outputLock stores reference to lock of the output queues
	// Variable preprocessQueue stores reference to the preprocessing queue
	// Variable outputQueue stores reference to the output queue
	private final Lock outputLock;
	private final BlockingQueue<String> preprocessQueue;
	private final Deque<Integer> outputQueue;

	// State variables for converting the queue input to integers
	private String inpString;
	private int qNum;

	// Constructor: Sets state variables, input and output queue references
	public PreProcessorConc(BlockingQueue<String> preprocessQueue, Deque<Integer> outputQueue, Lock outputLock) {
		this.preprocessQueue = preprocessQueue;
		this.outputQueue = outputQueue;
		this.outputLock = outputLock;
		this.inpString = "";
		this.qNum = -1;
	}

	// Function to continuously convert input strings to output integers in a thread safe manner
	//	Thread safety implemented using BlockedQueue and Lock from java.util.concurrent package
	public void run() {
		while (true) {
			try{
				// Obtain top of queue in a thread-safe manner
				//	Blocks if queue is empty
				inpString = preprocessQueue.take();
				qNum = inpString.charAt(0) - '0';
				
				// Synchronize on the output queue
				outputLock.lock();
					outputQueue.addLast(0);
					outputQueue.addLast( Integer.parseInt(inpString.substring(1), 2) );
				outputLock.unlock();
				// Release output queue locks to allow other threads to access
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

}
