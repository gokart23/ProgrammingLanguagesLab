import java.util.Deque;
import java.util.ArrayList;
import java.util.concurrent.locks.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DataProcessorConc implements Runnable {
	private static final int[] THRESHOLD = {100, 100000, 10000};

	private final int MODE, NUM_GENERATOR;
	private final Lock[] outputLocks;
	private final ArrayList<Deque<Integer>> outputQueues;
	private int output, cycle;

	public DataProcessorConc(ArrayList<Deque<Integer>> outputQueues, Lock[] outputLocks, int mode, int num_generator) {
		this.outputQueues = outputQueues;
		this.outputLocks = outputLocks;
		this.MODE = mode;
		this.cycle = 0;
		this.NUM_GENERATOR = num_generator;
	}

	public void run() {
		if (MODE == 0)		mean();
		else if (MODE == 1)	multiply();
		else				add();
	}

	private int getValConc(int idx) {
		int count=0, res=-1;
		outputLocks[idx].lock();
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
			// else System.out.println("\t\t\tPopped " + res);
			// System.out.println("\t\tgetValConc:\t (" + MODE + ") count " + count + " res " + res);
		}
		outputLocks[idx].unlock();		
		return res;
	} 

	private void mean() {
		long sum=0;
		Integer[] vals = new Integer[NUM_GENERATOR];
		while (true) {
			sum=0;
			try {
					for (int i = 0; i < NUM_GENERATOR; ) {
						vals[i] = getValConc(i);
						if (vals[i] >= 0) i++;						
					}

					String op = "AVG: #" + (cycle++) + "[";
					for (Integer val : vals) {
						sum += val;
						op += val + ", ";						
					}
					sum /= NUM_GENERATOR;
					op += "] - AVG=" + sum + " ";

					if ( sum >= THRESHOLD[MODE] )
						op += ":State detected from (avg)";
					else
						op += ":State not detected from (avg)";

					System.out.println(op);
					Thread.sleep(1000);
			} catch (Exception e) { e.printStackTrace(); }
		}	
	}

	private void multiply() {
		long prod=1;
		Integer[] vals = new Integer[NUM_GENERATOR];
		while (true) {
			prod=1;
			try {
					for (int i = 0; i < NUM_GENERATOR; ) {
						vals[i] = getValConc(i);
						if (vals[i] >= 0) i++;						
					}

					String op = "MUL: #" + (cycle++) + "[";
					for (Integer val : vals) {
						if (prod < THRESHOLD[MODE] || val == 0 )
							prod *= val;
						op += val + ", ";						
					}
					op += "] - ThresholdProd=" + prod + " ";

					if ( prod >= THRESHOLD[MODE] )
						op += ":State detected from (product)";
					else
						op += ":State not detected from (product)";

					System.out.println(op);
					Thread.sleep(1000);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	private void add() {
		long sum=0;
		Integer[] vals = new Integer[NUM_GENERATOR];
		while (true) {
			sum=0;
			try {
					for (int i = 0; i < NUM_GENERATOR; ) {
						vals[i] = getValConc(i);
						if (vals[i] >= 0) i++;						
					}

					String op = "SUM: #" + (cycle++) + "[";
					for (Integer val : vals) {
						sum += val;
						op += val + ", ";						
					}
					op += "] - Sum=" + sum + " ";

					if ( sum >= THRESHOLD[MODE] )
						op += ":State detected from (sum)";
					else
						op += ":State not detected from (sum)";

					System.out.println(op);
					Thread.sleep(1000);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}