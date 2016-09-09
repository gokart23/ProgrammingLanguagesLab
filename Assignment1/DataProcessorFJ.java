import java.util.Queue;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.*;

public class DataProcessorFJ implements Runnable {
	private static final int[] THRESHOLD = {100, 100000, 10000};

	private final int MODE, NUM_GENERATOR;
	private final ArrayList<Deque<Integer>> outputQueues;
	private final 
	private int output, cycle;

	public DataProcessorSynch(ArrayList<Deque<Integer>> outputQueues, int mode, int num_generator) {
		this.outputQueues = outputQueues;
		this.MODE = mode;
		this.cycle = 0;
		this.NUM_GENERATOR = num_generator;
	}

	public void run() {
		if (MODE == 0)		mean();
		else if (MODE == 1)	multiply();
		else				add();
	}

	private int getValSynchronized(int idx) {
		int count=0, res=-1;
		synchronized(outputQueues.get(idx)) {
			if (outputQueues.get(idx).size() > 0) {
				count = outputQueues.get(idx).removeFirst();
				res = outputQueues.get(idx).removeFirst();				

				if ( (count & (1 << MODE)) == 0)
					count = count | (1 << MODE);									
				else res = -1;
				
				if ( (count+1) != (1 << 3) ) {
					outputQueues.get(idx).addFirst(res);
					outputQueues.get(idx).addFirst(count);
				}
			}
		}
		return res;
	} 

	private void mean() {
		int sum=0, val=0;
		while (true) {
			sum=0; val=0;
			try {
					for (int i = 0; i < NUM_GENERATOR; ) {
						val = getValSynchronized(i);						
						if (val >= 0) {
							sum += val; 
							i++;
						}
					}
					if ( (sum/NUM_GENERATOR) >= THRESHOLD[MODE] )
						System.out.println("#" + cycle + ":State detected from (mean)");
					else
						System.out.println("#" + cycle + ":State not detected from (mean)");
					cycle++;
					// System.in.read();
					Thread.sleep(1000);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	private void multiply() {
		long prod=1, val=0;
		while (true) {
			prod=1; val=0;
			try {
					for (int i = 0; i < NUM_GENERATOR; ) {
						val = getValSynchronized(i);						
						if (val >= 0) {
							if (prod < THRESHOLD[MODE] || val == 0 )
								prod *= val;
							i++;
						}						
						// System.out.println("\tProd\t" + val + " <- " + i + " // " + prod);
					}
					if ( prod >= THRESHOLD[MODE] )
						System.out.println("\t#" + cycle + ":State detected from (product)");
					else
						System.out.println("\t#" + cycle + ":State not detected from (product)");
					cycle++;
					// System.in.read();
					Thread.sleep(1000);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	private void add() {
		int sum=0, val=0;
		while (true) {
			sum=0; val=0;
			try {
					for (int i = 0; i < NUM_GENERATOR; ) {
						val = getValSynchronized(i);						
						if (val >= 0) {
							sum += val; 
							i++;
						}
						// System.out.println("\tMean\t" + val + " <- " + i + " // " + sum);
					}
					if ( sum >= THRESHOLD[MODE] )
						System.out.println("\t\t#" + cycle + ":State detected from (addition)");
					else
						System.out.println("\t\t#" + cycle + ":State not detected from (addition)");
					cycle++;
					// System.in.read();
					Thread.sleep(1000);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}

class MergeSortFJ extends RecursiveTask<Integer[]> {

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
		Integer[] secondHalf = Arrays.copyOfRange(dataValues, dataValues.length / 2, dataValues.length)

		MergeSortFJ firstSubthread = new MergeSortFJ(this.invokerMode, firstHalf), secondSubthread = new MergeSortFJ(this.invokerMode, secondHalf);
		invokeAll(firstSubthread, secondSubthread);

		Integer[] firstResult = firstSubthread.join(), secondResult = secondSubthread.join();
		return combineSorted(firstResult, secondResult);
	}

	private Integer[] combineSorted(Integer[] first, Integer[] second) {
		Integer[] mergedArray = new Integer[first.length + second.length];
	}
}