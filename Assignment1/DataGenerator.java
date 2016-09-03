import java.util.Random;
import java.util.Queue;

public class DataGenerator implements Runnable{
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
					System.out.println("Generator#" + this.generatorNumber + ": Added " + rndString + "(" + preprocessQueue.size() + ")");
				} catch(Exception e) {	e.printStackTrace();	}
			}
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {}
		}
	}
}