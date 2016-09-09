import java.util.Queue;
import java.util.Deque;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MergeSortFJ extends RecursiveTask<Integer[]> {

	private final Integer invokerMode;
	private Integer[] dataValues;

	public MergeSortFJ(Integer invokerMode, Integer[] dataValues) {
		this.invokerMode = invokerMode;
		this.dataValues = dataValues;
	}

	@Override
	protected Integer[] compute() {
		if (dataValues.length == 1)
			return dataValues;
		
		Integer[] firstHalf = Arrays.copyOfRange(dataValues, 0, dataValues.length / 2);
		Integer[] secondHalf = Arrays.copyOfRange(dataValues, dataValues.length / 2, dataValues.length);

		MergeSortFJ firstSubthread = new MergeSortFJ(this.invokerMode, firstHalf), secondSubthread = new MergeSortFJ(this.invokerMode, secondHalf);
		invokeAll(firstSubthread, secondSubthread);

		Integer[] firstResult = firstSubthread.join(), secondResult = secondSubthread.join();
		return combineSorted(firstResult, secondResult);
	}

	private Integer[] combineSorted(Integer[] first, Integer[] second) {
		
		Integer firstHead = 0, secondHead = 0, mergeHead = 0;
		Integer[] mergedArray = new Integer[first.length + second.length];
		
	    while ((firstHead < first.length) && (secondHead < second.length)) {
		    if (first[firstHead] < second[secondHead])
	        	mergedArray[mergeHead++] = first[firstHead++];
	      	else 
	    	    mergedArray[mergeHead++] = second[secondHead++];
	    }
	  
	    for (; firstHead < first.length; )
	    	mergedArray[mergeHead++] = first[firstHead++];
	   	for (; secondHead < second.length; )
	   		mergedArray[mergeHead++] = second[secondHead++];

	   	return mergedArray;
	}

}