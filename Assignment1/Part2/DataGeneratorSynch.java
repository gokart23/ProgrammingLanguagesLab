/*
*	CS431: Programming Languages Lab
*	Assignment 1,	Part 1: Segment 1
*
*	Karthik Duddu, Mohit Chhajed
*	Group 25
*/

import java.util.Random;
import java.util.Queue;

/**
*	Class to generate data using Runnable and synchronized()
*/
public class DataGeneratorSynch implements Runnable{
	
	// Preprocessing Queue in which data is stored by the generator
	//	Thread-safe data sharing ensured using synchronized keyword
	private final Queue<String> preprocessQueue;
	
	// State variables of the object, used for generating data and identifying the generator
	private final Integer generatorNumber;
	private Random rng;
	private String rndString;


	// Constructor: Sets state variables and preprocessing queue reference
	public DataGeneratorSynch(Queue<String> preprocessQueue, int generatorNumber) {
		this.preprocessQueue = preprocessQueue;	
		this.generatorNumber = generatorNumber;
		this.rng = new Random();
		this.rndString = "";
	}

	// Function to continuously generate data and sleep for approximately 100ms 
	//	Executed in a new thread
	public void run() {
		while (true) {
			//Generate random 8-bit binary string		 rng.nextInt(256) 
			rndString = String.format("%8s", Integer.toBinaryString( rng.nextInt(256) ) ).replace(' ', '0');
			
			// Thread-safe data sharing ensured by using synchronized() keyword
			synchronized(preprocessQueue) {
				try{
					preprocessQueue.add(rndString);
					preprocessQueue.add(generatorNumber.toString());
				} catch(Exception e) {	e.printStackTrace();	}
			}
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {}
		}
	}
}