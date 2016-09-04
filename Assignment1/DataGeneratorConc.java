import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class DataGeneratorConc implements Runnable{
	private final BlockingQueue<String> preprocessQueue;
	private final Integer generatorNumber;
	private Random rng;
	private String rndString;

	public DataGeneratorConc(BlockingQueue<String> preprocessQueue, int generatorNumber) {
		this.preprocessQueue = preprocessQueue;	
		this.generatorNumber = generatorNumber;
		this.rng = new Random();
		this.rndString = "";
	}

	public void run() {
		while (true) {
			rndString = String.format("%8s", Integer.toBinaryString( rng.nextInt(256) ) ).replace(' ', '0');
			try{
				preprocessQueue.put(generatorNumber.toString() + rndString);				
				Thread.sleep(100);
				// System.out.println("Generator#" + this.generatorNumber + ": Added " + rndString + "(" + preprocessQueue.size() + ")");
			} catch(Exception e) {	e.printStackTrace();	}
		}
	}
}