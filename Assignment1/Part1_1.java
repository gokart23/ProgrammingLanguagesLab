import java.util.*;

public class Part1_1 {
	static final int NUM_GENERATOR = 10;
	public static void main(String[] args) {
		Queue<String> preprocessQueue = new LinkedList<String>();
		DataGenerator[] generators = new DataGenerator[NUM_GENERATOR];

		System.out.println("Main thread started");

		for(int i = 0; i < NUM_GENERATOR; i++) {
			generators[i] = new DataGenerator(preprocessQueue, i);
			(new Thread(generators[i])).start();
		}
	}
}


class DataGenerator implements Runnable{
	private final Queue<String> preprocessQueue;
	private final Integer generatorNumber;
	private Random rng;
	private String rndString;

	public DataGenerator(Queue<String> preprocessQueue, int generatorNumber) {
		this.preprocessQueue = preprocessQueue;	
		this.generatorNumber = generatorNumber;
		this.rng = new Random();
		this.rndString = "";
	}

	public void run() {
		while (true) {
			//Generate random 8-bit binary string		
			rndString = String.format("%8s", Integer.toBinaryString( rng.nextInt(256) ) ).replace(' ', '0');
			synchronized(preprocessQueue) {
				try{
					preprocessQueue.add(rndString);
					preprocessQueue.add(generatorNumber.toString());
					// System.out.println("Generator#" + this.generatorNumber + ": Added " + rndString + "(" + preprocessQueue.size() + ")");
				} catch(Exception e) {	e.printStackTrace();	}
			}
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {}
		}
	}
}