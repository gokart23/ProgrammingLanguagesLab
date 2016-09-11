/*
*	CS431: Programming Languages Lab
*	Assignment 1,	Part 2
*
*	Karthik Duddu, Mohit Chhajed
*	Group 25
*/

import java.util.Queue;
import java.util.Deque;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
*	Class to implement merge sort using Fork/Join Framework
*/
public class MergeSortFJ extends RecursiveTask<Integer[]> {

	// State variables for computing the merge sort and storing information about invoking thread
	private final Integer invokerMode;
	private Integer[] dataValues;

	//Constructor: Sets state variables
	public MergeSortFJ(Integer invokerMode, Integer[] dataValues) {
		this.invokerMode = invokerMode;
		this.dataValues = dataValues;
	}
	
	// Overriden function of RecursiveTask<Integer> that performs merge sort
	//	Recursive formulation: For single element, return element; Else, call recursively on both halves
	//	Returns: Sorted segment of array
	@Override
	protected Integer[] compute() {
		// Recursion base case: If single element, return
		if (dataValues.length == 1)
			return dataValues;
		
		// Recursive relation: If multiple elements, divide into halves and call recursively
		Integer[] firstHalf = Arrays.copyOfRange(dataValues, 0, dataValues.length / 2);
		Integer[] secondHalf = Arrays.copyOfRange(dataValues, dataValues.length / 2, dataValues.length);

		// Creating recursive subtasks
		MergeSortFJ firstSubthread = new MergeSortFJ(this.invokerMode, firstHalf), secondSubthread = new MergeSortFJ(this.invokerMode, secondHalf);
		// Invoking these tasks in the fork-join thread pool
		invokeAll(firstSubthread, secondSubthread);

		// Waiting for result computation from child threads
		Integer[] firstResult = firstSubthread.join(), secondResult = secondSubthread.join();

		// Returing combined sort results
		return combineSorted(firstResult, secondResult);
	}

	// Function for combining sorted values into a new array and returing
	private Integer[] combineSorted(Integer[] first, Integer[] second) {
		
		// State variables to linearly combine the sorted arrays
		Integer firstHead = 0, secondHead = 0, mergeHead = 0;

		// Output merged array
		Integer[] mergedArray = new Integer[first.length + second.length];
		
		// Merging until one or both lists are unfinished
	    while ((firstHead < first.length) && (secondHead < second.length)) {
		    if (first[firstHead] < second[secondHead])
	        	mergedArray[mergeHead++] = first[firstHead++];
	      	else 
	    	    mergedArray[mergeHead++] = second[secondHead++];
	    }
	  
	    // Merging first list, if unfinished
	    for (; firstHead < first.length; )
	    	mergedArray[mergeHead++] = first[firstHead++];
	    //Merging second list, if unfinished
	   	for (; secondHead < second.length; )
	   		mergedArray[mergeHead++] = second[secondHead++];

	   	return mergedArray;
	}

}