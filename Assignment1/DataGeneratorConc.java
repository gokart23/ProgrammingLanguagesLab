/*
*	CS431: Programming Languages Lab
*	Assignment 1,	Part 1: Segment 2
*
*	Karthik Duddu, Mohit Chhajed
*	Group 25
*/

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
*	Class to generate data using java.util.concurrent package
*/
public class DataGeneratorConc implements Runnable{

	// Preprocessing Queue in which data is stored by the generator
	//		Used a BlockingQueue, which part of the java.util.concurrent package
	private final BlockingQueue<String> preprocessQueue;

	// State variables of the object, used for generating data and identifying the generator
	private final Integer generatorNumber;
	private Random rng;
	private String rndString;

	// Constructor: Sets state variables and preprocessing queue reference
	public DataGeneratorConc(BlockingQueue<String> preprocessQueue, int generatorNumber) {
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
			try{
				// Thread-safe data sharing ensured by using BlockingQueue's put() method for sharing data
				preprocessQueue.put(generatorNumber.toString() + rndString);				
				Thread.sleep(100);
			} catch(Exception e) {	e.printStackTrace();	}
		}
	}
}